package IIS.wis2_backend.DTO.Response.Course;

import java.util.List;

import IIS.wis2_backend.DTO.Response.NestedDTOs.OverviewCourseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * DTO representing the courses a user is involved with.
 */
@AllArgsConstructor
@Builder
@Data
public class UserCoursesDTO {
    @NonNull
    private List<OverviewCourseDTO> supervisedCourses;

    @NonNull
    private List<OverviewCourseDTO> teachingCourses;

    @NonNull
    private List<OverviewCourseDTO> enrolledCourses;
}