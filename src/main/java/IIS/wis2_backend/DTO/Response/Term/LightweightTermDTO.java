package IIS.wis2_backend.DTO.Response.Term;

import java.time.LocalDateTime;

/**
 * DTO representing a lightweight version of a term. Used as a response in various endpoints.
 */
public record LightweightTermDTO(
    Long id,
    String name,
    LocalDateTime dateTime,
    Integer duration
) {}