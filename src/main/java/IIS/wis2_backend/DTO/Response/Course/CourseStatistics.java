package IIS.wis2_backend.DTO.Response.Course;

/**
 * DTO containing course statistics (well, just min/max prices).
 */
public record CourseStatistics(Double minPrice, Double maxPrice) {}