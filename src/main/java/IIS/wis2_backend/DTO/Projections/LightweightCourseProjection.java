package IIS.wis2_backend.DTO.Projections;

/**
 * Interface for projecting lightweight course data.
 */
public interface LightweightCourseProjection {
    Long getId();
    String getName();
    String getShortcut();
    Double getPrice();
    String getCompletedBy();
}