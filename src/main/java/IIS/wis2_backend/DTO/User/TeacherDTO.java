package IIS.wis2_backend.DTO.User;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Data Transfer Object for a teacher.
 */
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherDTO extends UserDTO {
    private String office;
}