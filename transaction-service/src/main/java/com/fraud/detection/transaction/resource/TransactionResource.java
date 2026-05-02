package com.fraud.detection.transaction.resource;

import com.fraud.detection.transaction.model.Transaction;
import com.fraud.detection.transaction.model.TransactionStatus;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import java.util.List;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Channel("transactions-out")
    Emitter<Transaction> transactionEmitter;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transaction> getAll() {
        return Transaction.listAll();
    }

    @POST
    public Response create(Transaction transaction) {
        // Force the DB to commit and CLOSE the connection immediately
        io.quarkus.narayana.jta.QuarkusTransaction.requiringNew().run(() -> {
            transaction.status = TransactionStatus.PENDING;
            transaction.persist();
        });

        // Now that the DB is safe, send to Kafka
        Transaction kafkaCopy = new Transaction();
        kafkaCopy.id = transaction.id;
        kafkaCopy.amount = transaction.amount;
        kafkaCopy.merchant = transaction.merchant;
        kafkaCopy.currency = transaction.currency;
        kafkaCopy.customerId = transaction.customerId;
        kafkaCopy.cardNumber = transaction.cardNumber;
        kafkaCopy.timestamp = transaction.timestamp;
        kafkaCopy.status = transaction.status;
        
        transactionEmitter.send(kafkaCopy);
        
        return Response.status(Response.Status.CREATED).entity(transaction).build();
    }
}
