package IIS.wis2_backend.Models.User;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
/**
 * Model representing one student. Inherits from User.
 */
@Entity
@Table(name = "STUDENTS")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Student extends User {
    /**
     * Student's GPA
     */
    Float gradeAverage;
}