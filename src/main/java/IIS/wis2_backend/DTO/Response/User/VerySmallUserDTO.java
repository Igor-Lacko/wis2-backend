package IIS.wis2_backend.DTO.Response.User;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO of a pending registration request.
 */
public record VerySmallUserDTO(
    @NotBlank
    String username,

    @NotBlank
    String firstName,

    @NotBlank
    String lastName
) {}