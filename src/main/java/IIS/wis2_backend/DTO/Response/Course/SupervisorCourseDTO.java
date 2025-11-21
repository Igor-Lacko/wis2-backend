package IIS.wis2_backend.DTO.Response.Course;

import java.util.Set;

import IIS.wis2_backend.DTO.Response.NestedDTOs.TeacherDTOForCourse;
import IIS.wis2_backend.Enum.CourseEndType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representation of a Course intended for supervisor view.
 */
@Data
@AllArgsConstructor
public class SupervisorCourseDTO {
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
    @NotEmpty
    private CourseEndType completedBy;

    @NotNull
    private Integer capacity;

    @NotNull
    private boolean autoregister;
}