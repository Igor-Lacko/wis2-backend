package IIS.wis2_backend.DTO.Schedule;

import java.util.List;

/**
 * Data Transfer Object for a schedule week.
 */
public record ScheduleWeekDTO(
    List<ScheduleItemDTO> items
) {}