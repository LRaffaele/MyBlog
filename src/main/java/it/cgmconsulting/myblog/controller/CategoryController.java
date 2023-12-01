package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.payload.request.ChangeVisibilityRequest;
import it.cgmconsulting.myblog.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("category")
@RequiredArgsConstructor
@SecurityRequirement(name = "myBlogSecurityScheme")
public class CategoryController {

    private final CategoryService categoryService;
    @PostMapping("name")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addCategory(@RequestParam @NotBlank @Size(max=50, min = 3) String categoryName){
        return categoryService.addCategory(categoryName);
    }
    @GetMapping("public")
    public ResponseEntity<?> getCategories(){
        return categoryService.getVisibileCategories();
    }

    @GetMapping("all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllCategories(){
        return categoryService.getAllCategories();
    }

    @PutMapping("change-visibility")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> changeVisibility(@RequestBody @Valid ChangeVisibilityRequest request){
        return categoryService.changeVisibility(request);
    }

}
