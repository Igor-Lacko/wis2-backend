package IIS.wis2_backend.Models.Lesson;

import java.util.Set;

import IIS.wis2_backend.Models.Relational.StudentLab;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Model representing a lab lesson.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Lab extends Lesson {
    /**
     * Min lab points you HAVE to get. (Will be 0 in most cases irl i guess).
     */
    @Column(nullable = false)
    private Integer minPoints;

    /**
     * Max lab points you CAN get. Can also be 0 if the labs are just for, idk, practicing on examples.
     */
    @Column(nullable = false)
    private Integer maxPoints;

    /**
     * Registered students for this lab.
     */
    @OneToMany
    private Set<StudentLab> students;
}