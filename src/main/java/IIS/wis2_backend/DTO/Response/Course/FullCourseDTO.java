package IIS.wis2_backend.DTO.Response.Course;

import java.util.Set;

import IIS.wis2_backend.DTO.Response.NestedDTOs.TeacherDTOForCourse;
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
    @NotEmpty
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

    private TeacherDTOForCourse supervisor;

    @NotNull
    private Set<TeacherDTOForCourse> teachers;

    @NotNull
    @NotEmpty
    private String completedBy;
}