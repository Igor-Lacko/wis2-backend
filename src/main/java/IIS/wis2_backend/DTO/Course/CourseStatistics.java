package IIS.wis2_backend.DTO.Course;

/**
 * DTO containing course statistics (well, just min/max prices).
 */
public record CourseStatistics(Double minPrice, Double maxPrice) {}