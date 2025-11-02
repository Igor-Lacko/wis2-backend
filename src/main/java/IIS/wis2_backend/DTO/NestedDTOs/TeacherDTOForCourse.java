package IIS.wis2_backend.DTO.NestedDTOs;

/**
 * DTO representing necessary teacher details for a public course page.
 */
public record TeacherDTOForCourse(
        Long id,
        String firstName,
        String lastName) {
}