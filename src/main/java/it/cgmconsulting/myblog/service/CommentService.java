package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Comment;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.payload.request.CommentRequest;
import it.cgmconsulting.myblog.repository.CommentRepository;
import it.cgmconsulting.myblog.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public ResponseEntity<?> save(CommentRequest request, UserPrincipal principal){
        Comment comment = new Comment(
                postService.findVisiblePost(request.getPostId(), LocalDateTime.now()),
                new User(principal.getId()),
                request.getComment(),
                request.getParentId() == null ? null : findCommentNotCensored(request.getParentId())
        );
        commentRepository.save(comment);
        return new ResponseEntity("New comment has been added to the post", HttpStatus.CREATED);
    }

    protected Comment findCommentNotCensored(long commentId){
        Comment comment = commentRepository.findByIdAndCensoredFalse(commentId).orElseThrow(
        ()-> new ResourceNotFoundException("Comment", "id", commentId));
        return comment;
    }

    public ResponseEntity getComments(long postId){
        return new ResponseEntity(commentRepository.getComments(postId), HttpStatus.OK);
    }
}
