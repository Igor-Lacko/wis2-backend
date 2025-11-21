package IIS.wis2_backend.DTO.Response.Projections;

/**
 * Projection interface for getting teacher details for a course.
 */
public interface TeacherForCourseProjection {
    String getUsername();
    String getFirstName();
    String getLastName();
}