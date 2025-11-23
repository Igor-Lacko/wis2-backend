package IIS.wis2_backend.DTO.Response.Course;

import IIS.wis2_backend.Enum.CourseEndType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for a course in admin view.
 */
@Data
@AllArgsConstructor
@Builder
public class AdminCourseDTO {
    private Long id;

    private String name;

    private String shortcut;

    private CourseEndType completedBy;

    private Integer capacity;

    private Integer enrolledStudents;

    private String supervisorUsername;
}