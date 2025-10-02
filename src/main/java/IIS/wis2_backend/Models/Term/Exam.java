package IIS.wis2_backend.Models.Term;

import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing an exam.
 */
@Entity
@Table(name = "EXAMS")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam extends Term {
    /**
     * Number of exam attempts.
     */
    Integer attempts;
}