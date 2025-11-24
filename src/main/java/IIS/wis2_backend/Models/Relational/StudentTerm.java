package IIS.wis2_backend.Models.Relational;

import IIS.wis2_backend.Models.Term.Term;
import IIS.wis2_backend.Models.User.Wis2User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing a relation student --> studentterm <-- term
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
    @JoinColumn(nullable = false, name = "student_id")
    private Wis2User student;

    /**
     * The term in the relation.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "term_id")
    private Term term;

    /**
     * Points the student has earned for this term. Null if not graded yet or the term is an lecture.
     */
    private Integer points;
}