package IIS.wis2_backend.DTO.Response.Projections;

/**
 * Interface for projecting lightweight course data.
 */
public interface LightweightCourseProjection {
    String getName();
    String getShortcut();
    Double getPrice();
    String getCompletedBy();
}