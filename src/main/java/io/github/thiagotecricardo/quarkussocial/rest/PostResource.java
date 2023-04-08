package io.github.thiagotecricardo.quarkussocial.rest;

import io.github.thiagotecricardo.quarkussocial.domain.model.Post;
import io.github.thiagotecricardo.quarkussocial.domain.model.User;
import io.github.thiagotecricardo.quarkussocial.domain.repository.FollowerRepository;
import io.github.thiagotecricardo.quarkussocial.domain.repository.PostRepository;
import io.github.thiagotecricardo.quarkussocial.domain.repository.UserRepository;
import io.github.thiagotecricardo.quarkussocial.rest.dto.CreatePostRequest;
import io.github.thiagotecricardo.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository ;
    private FollowerRepository followerRepository;
    private PostRepository postRepository;
    @Inject
    public PostResource(UserRepository userRepository,
                        PostRepository postRepository,
                        FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request){

        User user = userRepository.findById(userId);

        if(user == null){

            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);

        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId  ){
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (followerId == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("voce esqueceu do header")
                    .build();
        }

        User follower = userRepository.findById(followerId);

        if (follower == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Seguidor inexistente")
                    .build();
        }

        boolean followers = followerRepository.follower(follower, user);

        if (!followers){
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Voce nao pode ver esse post")
                    .build();

        }
        PanacheQuery<Post> query = postRepository.find(
                "user", Sort.by("dateTime", Sort.Direction.Descending) ,user);
        List<Post> list = query.list();

        var postResposnseList= list.stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(postResposnseList).build();
    }


}
