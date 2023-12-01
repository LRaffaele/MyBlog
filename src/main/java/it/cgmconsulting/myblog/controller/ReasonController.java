package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.payload.request.ReasonRequest;
import it.cgmconsulting.myblog.service.ReasonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("reason")
@RequiredArgsConstructor
@SecurityRequirement(name = "myBlogSecurityScheme")
public class ReasonController {

    private final ReasonService reasonService;

    @PostMapping("create")
    @PreAuthorize("hasRole('ROLE_ADMIN') or ('ROLE_MODERATOR')")
    public ResponseEntity<?> createReason (@RequestBody @Valid ReasonRequest request){
        return  reasonService.createReason(request);
    }

    @PutMapping("update")
    @PreAuthorize("hasRole('ROLE_ADMIN') or ('ROLE_MODERATOR')")
    public ResponseEntity<?> updateReason (@RequestBody @Valid ReasonRequest request){
        return reasonService.updateReason(request);
    }

    @PutMapping("remove") // invalida la reason settando solo una dend tate
    @PreAuthorize("hasRole('ROLE_ADMIN') or ('ROLE_MODERATOR')")
    public ResponseEntity<?> removeReason (@RequestParam @NotBlank @Size(min=5, max=50) String reason, @RequestParam @NotNull @FutureOrPresent LocalDate endDate){
        return reasonService.removeReason(reason,endDate);
    }

    @GetMapping("get-all")
    @PreAuthorize("hasRole('ROLE_READER)")
    public ResponseEntity<?> getReasons(){
        return reasonService.getReasons();
    }

}
