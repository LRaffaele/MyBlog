package it.cgmconsulting.myblog.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentRequest {

    private long postId;
    @NotBlank @Size(min=2, max=255)
    private String comment;
    private Long parentId;
}
