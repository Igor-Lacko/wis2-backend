package IIS.wis2_backend.DTO.Response.Course;

import java.util.List;

import IIS.wis2_backend.DTO.Response.NestedDTOs.OverviewCourseDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO representing the courses a user is involved with.
 */
@AllArgsConstructor
@Builder
@Data
public class UserCoursesDTO {
    @NotNull
    private List<OverviewCourseDTO> supervisedCourses;

    @NotNull
    private List<OverviewCourseDTO> teachingCourses;

    @NotNull
    private List<OverviewCourseDTO> enrolledCourses;
}