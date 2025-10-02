package IIS.wis2_backend.Models.Lesson;

import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing a lab lesson.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lab extends Lesson {
    /**
     * Min lab points you HAVE to get. (Will be 0 in most cases irl i guess).
     */
    private Integer minPoints;

    /**
     * Max lab points you CAN get. Can also be 0 if the labs are just for, idk, practicing on examples.
     */
    private Integer maxPoints;
}