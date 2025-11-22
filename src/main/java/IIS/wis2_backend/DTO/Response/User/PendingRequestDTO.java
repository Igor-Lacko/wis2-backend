package IIS.wis2_backend.DTO.Response.User;

import IIS.wis2_backend.Enum.PendingRequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a pending request for a user.
 */
public record PendingRequestDTO (
    @NotBlank String shortcut,
    @NotNull PendingRequestType type
) {}