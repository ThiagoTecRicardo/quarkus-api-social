package io.github.thiagotecricardo.quarkussocial.rest;


import io.github.thiagotecricardo.quarkussocial.domain.model.User;
import io.github.thiagotecricardo.quarkussocial.domain.repository.UserRepository;
import io.github.thiagotecricardo.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.h2.constraint.Constraint;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {

    private UserRepository repository;
    private final Validator validator;

    @Inject
    public UsersResource(UserRepository repository, Validator  validator){

        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest){

      Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);

      if (!violations.isEmpty()){

          ConstraintViolation<CreateUserRequest> erro = violations.stream().findAny().get();
          String errorMessage = erro.getMessage();
          return Response.status(400).entity(errorMessage).build();
      }

        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        repository.persist(user);

        return Response.ok(user).build();
    }

    @GET
    public Response listAllUsers(){

        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }
    
    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser( @PathParam("id") Long id ){
        User user = repository.findById(id);

        if(user != null){
            repository.delete(user);
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();

    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser( @PathParam("id") Long id,  CreateUserRequest userData ){
        User user = repository.findById(id);

        if(user != null){
            user.setName(userData.getName());
            user.setAge(userData.getAge());

            return Response.ok().build();

        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
