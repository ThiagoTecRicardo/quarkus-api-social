package io.github.thiagotecricardo.quarkussocial.rest;

import io.github.thiagotecricardo.quarkussocial.domain.model.Follower;
import io.github.thiagotecricardo.quarkussocial.domain.repository.FollowerRepository;
import io.github.thiagotecricardo.quarkussocial.domain.repository.UserRepository;
import io.github.thiagotecricardo.quarkussocial.rest.dto.FollowerRequest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;
    @Inject
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository){
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(
            @PathParam("userId") Long userId, FollowerRequest followerRequest){

        if (userId.equals(followerRequest.getFollowerId())){
            return Response.status(Response.Status.CONFLICT).entity("Voce não pode seguir voce mesmo").build();
        }

        var user = userRepository.findById(userId);
        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var follower = userRepository.findById(followerRequest.getFollowerId());

        boolean followes = followerRepository.follower(follower, user);

        if (!followes){

            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);

            followerRepository.persist(entity);

        }


        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
