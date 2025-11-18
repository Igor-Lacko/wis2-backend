package IIS.wis2_backend.DTO.Request.Lesson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO for creating a lesson.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabCreationDTO extends LessonCreationDTO {
    private Integer minPoints;
    private Integer maxPoints;
}