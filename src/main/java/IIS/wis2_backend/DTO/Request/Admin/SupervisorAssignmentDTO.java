package IIS.wis2_backend.DTO.Request.Admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for assigning a supervisor to a course. Only usable by the admin.
 */
@Data
public class SupervisorAssignmentDTO {
    @NotNull Long courseId;
    @NotBlank String supervisorUsername;
}
