package IIS.wis2_backend.DTO.Request.Lesson;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO for creating a lesson.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LessonCreationDTO {
    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    private Integer duration;

    @NotBlank
    private String lecturerUsername;

    @NotBlank
    private String courseShortcut;

    @NotBlank
    private String roomShortcut;

    @NotNull
    private Boolean autoregistered;
}