package org.as1iva.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequestDto {

    @NotBlank(message = "Login cannot be empty. Please enter a valid login")
    @Size(min = 5, max = 15, message = "Length must be between 5 and 15")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$", message = "Login must contain both English letters and numbers")
    private String login;

    @NotBlank(message = "Password cannot be empty. Please enter a valid password")
    @Size(min = 5, message = "Length must be at least 8")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$", message = "Password must contain both English letters and numbers")
    private String password;

    private String repeatPassword;
}
