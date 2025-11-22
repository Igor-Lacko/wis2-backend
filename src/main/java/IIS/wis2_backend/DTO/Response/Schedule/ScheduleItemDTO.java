package IIS.wis2_backend.DTO.Response.Schedule;

import java.time.LocalDateTime;

import IIS.wis2_backend.Enum.TermType;

/**
 * Data Transfer Object for a schedule item.
 */
public record ScheduleItemDTO(
    LocalDateTime startDate,
    LocalDateTime endDate,
    String course,
    TermType termType
) {}