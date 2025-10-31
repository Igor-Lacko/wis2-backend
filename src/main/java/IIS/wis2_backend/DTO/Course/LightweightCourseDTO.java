package IIS.wis2_backend.DTO.Course;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing a lightweight version of a Course (to be used in lists).
 */
@Data
@AllArgsConstructor
public class LightweightCourseDTO {
    @NotNull
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Double price;

    @NotNull
    @NotEmpty
    private String shortcut;
}