package IIS.wis2_backend.DTO.Response.Schedule;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for a schedule item.
 */
public record ScheduleItemDTO(
    long id,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String course,
    String type
) {}