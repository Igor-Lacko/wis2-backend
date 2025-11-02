package IIS.wis2_backend.DTO.User;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

import IIS.wis2_backend.DTO.NestedDTOs.CourseDTOForTeacher;
import IIS.wis2_backend.DTO.NestedDTOs.OfficeDTOForTeacher;

/**
 * Data Transfer Object for a teacher.
 */
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherDTO extends UserDTO {
    private OfficeDTOForTeacher office;
    private Set<CourseDTOForTeacher> supervisedCourses;
    private Set<CourseDTOForTeacher> taughtCourses;
}