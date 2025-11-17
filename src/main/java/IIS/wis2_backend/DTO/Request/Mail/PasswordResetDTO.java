package IIS.wis2_backend.DTO.Request.Mail;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for password reset requests.
 */
public record PasswordResetDTO(
    @NotBlank String password,
    @NotBlank String confirmPassword,
    @NotBlank String token
) {}