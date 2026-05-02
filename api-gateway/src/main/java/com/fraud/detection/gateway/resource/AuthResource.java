package com.fraud.detection.gateway.resource;

import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.HashSet;
import org.eclipse.microprofile.jwt.Claims;

@Path("/auth")
public class AuthResource {

    @GET
    @Path("/token")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String generateToken() {
        return Jwt.issuer("https://example.com/issuer") 
           .upn("jdoe@example.com") 
           .groups(new HashSet<>(Arrays.asList("User", "Admin"))) 
           .claim(Claims.birthdate.name(), "2001-07-13") 
           .sign();
    }
}
