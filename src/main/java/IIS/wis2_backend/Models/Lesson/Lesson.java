package IIS.wis2_backend.Models.Lesson;

import java.time.LocalDateTime;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Room.Room;
import IIS.wis2_backend.Models.User.Teacher;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Model representing an abstract lesson (e.g. lectures or labs)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
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
    @Column(nullable = false)
    private LocalDateTime dateTime;

    /**
     * Lesson duration in minutes.
     */
    @Column(nullable = false)
    private Integer duration;

    /**
     * The teacher for this lesson.
     */
    @ManyToOne
    @Column(nullable = false)
    private Teacher lecturer;

    /**
     * The course this lesson belongs to.
     */
    @ManyToOne
    @Column(nullable = false)
    private Course course;

    /**
     * The room this lesson takes place in.
     */
    @ManyToOne
    @Column(nullable = false)
    private Room room;

    /**
     * If the lesson is autoregistered.
     */
    @Column(nullable = false)
    private Boolean autoregistered;
}