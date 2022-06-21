package io.github.thiagotecricardo.quarkussocial.rest;


import io.github.thiagotecricardo.quarkussocial.rest.dto.CreateUserRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {

    @POST
    public Response createUser(CreateUserRequest userRequest){
        return Response.ok(userRequest).build();
    }

    @GET
    public Response listAllUsers(){
        return Response.ok().build();
    }
}
