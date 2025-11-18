package IIS.wis2_backend.Models.Relational;

import IIS.wis2_backend.Models.Term.Term;
import IIS.wis2_backend.Models.User.Student;
import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing a relation --> student studentterm <-- term
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentTerm {
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
     * The term in the relation.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Term term;

    /**
     * Points the student has earned for this term. Null if not graded yet.
     */
    private Integer points;
}