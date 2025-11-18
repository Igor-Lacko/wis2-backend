package IIS.wis2_backend.Models.Relational;

import IIS.wis2_backend.Models.Lesson.Lab;
import IIS.wis2_backend.Models.User.Student;
import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing a relation student --> studentlab <-- lab
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentLab {
    /**
     * Relation ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The student in the relation.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Student student;

    /**
     * The lab in the relation.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Lab lab;

    /**
     * Points the student has earned for this lab. Null if not graded yet.
     */
    private Integer points;
}