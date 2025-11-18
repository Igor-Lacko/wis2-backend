package IIS.wis2_backend.Models.Lesson;

import java.time.LocalDateTime;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.User.Teacher;
import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing an abstract lesson (e.g. lectures or labs)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Lesson {
    /**
     * Lesson ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Lesson datetime.
     */
    private LocalDateTime dateTime;

    /**
     * Lesson duration in minutes.
     */
    private Integer duration;

    /**
     * The teacher for this lesson.
     */
    @ManyToOne
    private Teacher lecturer;

    /**
     * The course this lesson belongs to.
     */
    @ManyToOne
    private Course course;
}