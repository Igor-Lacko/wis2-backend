package IIS.wis2_backend.DTO.Response.NestedDTOs;

/**
 * DTO representing an office in getting a public profile for a teacher, so that it can be linked.
 */
public record OfficeDTOForTeacher(
    Long id,
    String shortcut
) {}