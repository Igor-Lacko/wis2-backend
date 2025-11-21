package IIS.wis2_backend.DTO.Request.Course;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for updating course details.
 */
@Data
public class CourseDetailsUpdateDTO {
    private String description;

    @NotNull
    @DecimalMin("0.0")
    private Double price;

    @NotNull
    @DecimalMin("1")
    private Integer capacity;

    @NotNull
    private Boolean autoregister;
}