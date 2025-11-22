package IIS.wis2_backend.DTO.Response.Room;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing an available room.
 */
public record AvailableRoomDTO(
    @NotBlank String shortcut,
    @NotBlank String building,
    @NotBlank String floor
) {}