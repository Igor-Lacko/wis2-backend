package IIS.wis2_backend.Models.Lesson;

import java.sql.Date;
import java.util.Set;

import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.User.Student;
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
    private Date datetime;

    /**
     * Lesson duration in minutes.
     */
    private Integer duration;

    /**
     * Students registered for the lesson.
     */
    @OneToMany
    private Set<Student> registeredStudents;

    /**
     * The teacher for this lesson.
     */
    @ManyToOne
    private Teacher supervisor;

    /**
     * The course this lesson belongs to.
     */
    @ManyToOne
    private Course course;
}