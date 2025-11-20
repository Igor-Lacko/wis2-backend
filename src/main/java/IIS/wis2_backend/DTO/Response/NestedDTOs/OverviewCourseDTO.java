package IIS.wis2_backend.DTO.Response.NestedDTOs;

/**
 * DTO representing an overview of a course with its name and shortcut.
 */
public record OverviewCourseDTO (
    String name,
    String shortcut
) {}