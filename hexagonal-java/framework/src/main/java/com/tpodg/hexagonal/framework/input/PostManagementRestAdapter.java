package com.tpodg.hexagonal.framework.input;

import com.tpodg.hexagonal.application.port.input.PostManagementInputPort;
import com.tpodg.hexagonal.application.port.output.PostManagementOutputPort;
import com.tpodg.hexagonal.application.usecase.PostManagementUseCase;
import com.tpodg.hexagonal.domain.entity.Comment;
import com.tpodg.hexagonal.domain.entity.Post;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.stream.Collectors;

@Path("/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostManagementRestAdapter {

    private final PostManagementUseCase postManagementUseCase;

    public PostManagementRestAdapter(PostManagementOutputPort postManagementOutputPort) {
        this.postManagementUseCase = new PostManagementInputPort(postManagementOutputPort);
    }

    @GET
    @Path("/{id}")
    public PostDto retrieve(@PathParam("id") Long id) {
        Post retrieved = postManagementUseCase.retrieve(id);
        return new PostDto(retrieved.id(), retrieved.title(), retrieved.content());
    }

    @POST
    public PostDto create(PostDto post) {
        Post created = postManagementUseCase.persist(postManagementUseCase.create(post.title(), post.content()));
        return new PostDto(created.id(), created.title(), created.content());
    }

    @POST
    @Path("/{id}/comment")
    public CommentDto comment(@PathParam("id") Long postId, CommentDto comment) {
        Comment created = postManagementUseCase.addComment(new Comment(null, comment.content(), postId));
        return new CommentDto(created.id(), created.content(), created.postId());
    }

    @GET
    @Path("/{id}/comments")
    public Collection<CommentDto> comments(@PathParam("id") Long postId) {
        Collection<Comment> comments = postManagementUseCase.listComments(postId);
        return comments.stream()
                .map(comment -> new CommentDto(comment.id(), comment.content(), comment.postId()))
                .collect(Collectors.toSet());
    }
}