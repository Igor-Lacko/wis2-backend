package IIS.wis2_backend.DTO.Response.NestedDTOs;

/**
 * DTO representing necessary teacher details for a public course page.
 */
public record TeacherDTOForCourse(
        String username,
        String firstName,
        String lastName) {
}