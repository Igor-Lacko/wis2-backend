package IIS.wis2_backend.DTO.Request.Lesson;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a lesson.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonCreationDTO {
    @NotNull
    private LocalDateTime dateTime;
    @NotNull
    private Integer duration;
    @NotBlank
    private String lecturerUsername;
    @NotBlank
    private String courseShortcut;
}