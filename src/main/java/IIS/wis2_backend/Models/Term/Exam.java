package IIS.wis2_backend.Models.Term;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Model representing an exam.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Exam extends Term {
    /**
     * Which exam attempt it is.
     */
    Integer attempt;
}