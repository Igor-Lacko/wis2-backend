package IIS.wis2_backend.DTO.Projections;

/**
 * Projection interface for getting teacher details for a course.
 */
public interface TeacherForCourseProjection {
    Long getId();
    String getFirstName();
    String getLastName();
}