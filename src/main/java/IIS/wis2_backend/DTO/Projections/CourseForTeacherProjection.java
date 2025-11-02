package IIS.wis2_backend.DTO.Projections;

/**
 * Projection interface for getting course details for a teacher.
 */
public interface CourseForTeacherProjection {
    Long getId();

    String getName();

    String getShortcut();
}