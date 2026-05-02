package com.fraud.detection.gateway.proxy;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import java.util.List;

@RegisterRestClient(baseUri = "http://transaction-service:8081")
@Path("/transactions")
public interface TransactionServiceProxy {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Object> getAll();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Object create(Object transaction);
}
