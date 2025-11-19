package IIS.wis2_backend.DTO.Request.Schedule;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for requesting a week of a schedule.
 */
public record ScheduleRequestDTO(
        @NotBlank String shortcutOrUsername,
        @NotNull LocalDate weekStart) {
}