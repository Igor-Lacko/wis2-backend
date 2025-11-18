package IIS.wis2_backend.DTO.Response.Lesson;

import java.time.LocalDateTime;

/**
 * DTO representing a lightweight version of a lesson.
 */
public record LightweightLessonDTO(
    Long id,
    LocalDateTime dateTime,
    Integer duration,
    String roomShortcut
) {}