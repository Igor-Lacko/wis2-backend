package IIS.wis2_backend.DTO.Response.Course;

import java.util.List;

import IIS.wis2_backend.DTO.Response.User.VerySmallUserDTO;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a list of pending registration requests for a course.
 */
public record PendingRequestsListDTO(
    long enrolledCount,
    @NotNull List<VerySmallUserDTO> pendingRequests
) {}