package IIS.wis2_backend.DTO.Course;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representation of a Course.
 */
@Data
@AllArgsConstructor
public class FullCourseDTO {
    @NotNull
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Double price;

    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    @NotEmpty
    private String shortcut;

    @NotNull
    private String supervisor;

    @NotNull
    private Set<String> teachers;

    @NotNull
    @NotEmpty
    private String completedBy;
}