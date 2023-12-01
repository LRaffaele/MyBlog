package it.cgmconsulting.myblog.payload.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpRequest {

    @NotBlank  @Size (min=6, max=20)
    private String username;

    @Pattern(regexp = "^[a-zA-Z0-9]{6,10}$",
            message = "La password inserita può contenere solo caratteri alfanumerici. La lunghezza deve essere compresa tra 6 e 10") // pattern regex impostato per la password
    private String password;

    @Email @NotBlank
    private String email;
}
