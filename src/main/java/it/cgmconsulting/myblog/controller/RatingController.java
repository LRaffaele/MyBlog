package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.RatingService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rating")
@RequiredArgsConstructor
@SecurityRequirement(name = "myBlogSecurityScheme")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{postId}/{rate}")
    @PreAuthorize("hasRole('ROLE_READER')")
    public ResponseEntity<?> addRating (
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable long postId,
            @PathVariable @Min(1) @Max(5) byte rate){
        return ratingService.addRating(principal.getId(), postId, rate);
    }
}
