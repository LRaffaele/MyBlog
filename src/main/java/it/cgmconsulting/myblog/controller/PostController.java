package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.entity.common.ImagePosition;
import it.cgmconsulting.myblog.payload.request.PostRequest;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("post")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "myBlogSecurityScheme")
public class PostController {

    private final PostService postService;

    @PostMapping("create")
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> createPost(@RequestBody @Valid PostRequest request, @AuthenticationPrincipal UserPrincipal principal){
        return postService.createPost(request, principal);
    }

    @CacheEvict(value = "postByCategories", allEntries = true)
    @PutMapping ("update/{id}")
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> updatePost(@PathVariable long id, @RequestBody @Valid PostRequest request, @AuthenticationPrincipal UserPrincipal principal){
        return postService.updatePost(id, request, principal);

    }

    @CacheEvict(value = "postByCategories", allEntries = true)
    @PutMapping("publish/{postId}")
    @PreAuthorize("hasRole('ROLE_CHIEF_EDITOR')")
    public ResponseEntity<?> publishPost(@PathVariable long postId, @RequestParam(required = false) LocalDateTime publishedAt){
            return postService.publishPost(postId, publishedAt);
    }

    @CacheEvict(value = "postByCategories", allEntries = true)
    @PutMapping("add-categories/{postId}") // aggiunge o modifica categorie associate ad un post
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> addCategories(@PathVariable long postId, @RequestBody @NotEmpty Set<String> categories){
        return postService.addCategories(postId, categories);
    }

    @CacheEvict(value = "postByCategories", allEntries = true)
    @PutMapping("remove-all-categories/{postId}") // elimina tutte le categorie associate ad un post
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> addCategories(@PathVariable long postId){
        return postService.removeCategories(postId);
    }

    @GetMapping("/public/get-boxes")
    public ResponseEntity<?> getBoxes(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "3") int pageSize,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "PRE") String imagePosition
    ){
        return postService.getPostBoxes(pageNumber, pageSize, direction, sortBy, imagePosition);
    }

    @GetMapping("/public/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable long postId, @RequestParam(defaultValue = "HDR") String imagePosition){
        return postService.getPostDetail(postId, imagePosition);
    }

    @Cacheable("postByCategories")
    @GetMapping("/public/get-posts-by-category")
    public ResponseEntity<?> getPostsByCategory(@RequestParam String categoryName){
        log.info("\n\n ------------------------------POST BY CATEGORIES--------------\n\n");
        return postService.getPostsByCategory(categoryName);
    }

    @GetMapping("/public/get-posts-by-keyword")
    public ResponseEntity<?> getPostByKeyword (
            @RequestParam String keyword,
            @RequestParam (defaultValue = "false") boolean isCaseSensitive,
            @RequestParam (defaultValue = "false") boolean isExactMatch,
            @RequestParam (defaultValue = "PRE") ImagePosition position
    ){
        return postService.getPostByKeyword(keyword,isCaseSensitive,isExactMatch,position);
    }

    @GetMapping("/public/get-posts-by-author")
    public ResponseEntity<?> getPostsByAuthor(@RequestParam String author){
        return postService.getPostsByAuthor(author);
    }

    @GetMapping("/public/get-most-rated-in-period")
    public ResponseEntity<?> getMostRatedInPeriod(
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end ){
        return postService.getMostRatedInPeriod(start, end);
    }
}
