package IIS.wis2_backend.DTO.Schedule;

import java.time.LocalDate;

/**
 * Data Transfer Object for a schedule item.
 */
public record ScheduleItemDTO(
    long id,
    LocalDate date,
    String course,
    String type
) {}