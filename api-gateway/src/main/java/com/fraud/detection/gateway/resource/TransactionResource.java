package com.fraud.detection.gateway.resource;

import com.fraud.detection.gateway.proxy.TransactionServiceProxy;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Inject
    @RestClient
    TransactionServiceProxy proxy;

    @GET
    public Response getAll() {
        return Response.ok(proxy.getAll()).build();
    }

    @POST
    public Response create(Object transaction) {
        return Response.status(Response.Status.CREATED).entity(proxy.create(transaction)).build();
    }
}
