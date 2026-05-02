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
@Produces(MediaType.APPLICATION_BITSTREAM)
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
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Transaction transaction) {
        transaction.status = TransactionStatus.PENDING;
        transaction.persist();
        
        // Send to Kafka for fraud analysis
        transactionEmitter.send(transaction);
        
        return Response.status(Response.Status.CREATED).entity(transaction).build();
    }
}
