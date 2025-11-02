package IIS.wis2_backend.DTO.NestedDTOs;

/**
 * DTO representing a course in getting a public profile for a teacher.
 */
public record CourseDTOForTeacher(
    Long id,
    String name,
    String shortcut
) {}