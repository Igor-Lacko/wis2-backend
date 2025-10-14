package IIS.wis2_backend.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for resending activation emails.
 */
public record ResendDTO(
    @Email @NotBlank String email
) {}