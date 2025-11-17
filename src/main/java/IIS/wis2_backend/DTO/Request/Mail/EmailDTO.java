package IIS.wis2_backend.DTO.Request.Mail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for sending activation and password reset emails.
 */
public record EmailDTO(
    @Email @NotBlank String email
) {}