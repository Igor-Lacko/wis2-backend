package IIS.wis2_backend.DTO.Response.Course;

import java.util.Set;

import IIS.wis2_backend.DTO.Response.Projections.OverviewCourseProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

/**
 * DTO representing the courses a user is involved with.
 */
@AllArgsConstructor
@Builder
public class UserCoursesDTO {
    @NonNull
    private Set<OverviewCourseProjection> supervisedCourses;

    @NonNull
    private Set<OverviewCourseProjection> teachingCourses;

    @NonNull
    private Set<OverviewCourseProjection> enrolledCourses;
}