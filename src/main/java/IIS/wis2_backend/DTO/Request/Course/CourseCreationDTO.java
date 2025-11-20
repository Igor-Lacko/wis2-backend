package IIS.wis2_backend.DTO.Request.Course;

import IIS.wis2_backend.Enum.CourseEndType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a course.
 */
public record CourseCreationDTO(
    @NotBlank @Size(min = 5, max = 100) String name,
    @NotBlank @Size(min = 3, max = 10) String shortcut,
    @NotNull @DecimalMin("1") Integer capacity,
    @NotNull @DecimalMin("0.0") Double price,
    @NotNull CourseEndType type,
    @NotNull Boolean autoregister
) {
}