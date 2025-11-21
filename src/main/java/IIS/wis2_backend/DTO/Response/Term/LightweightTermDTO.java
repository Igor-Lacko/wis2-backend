package IIS.wis2_backend.DTO.Response.Term;

import java.time.LocalDateTime;

import IIS.wis2_backend.Enum.TermType;

/**
 * DTO representing a lightweight version of a term. Used as a response in various endpoints.
 */
public record LightweightTermDTO(
    Long id,
    String name,
    LocalDateTime dateTime,
    Integer duration,
    String roomShortcut,
    TermType termType
) {}