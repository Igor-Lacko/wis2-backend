package IIS.wis2_backend.Models.Assignment;

import java.sql.Date;
import java.util.Set;

import IIS.wis2_backend.Models.User.*;
import IIS.wis2_backend.Models.Course;

import jakarta.persistence.*;
import lombok.*;

/**
 * Abstract model representing an assignment (e.g. projects, homework).
 */
@Entity
@Table(name = "ASSIGNMENTS")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
public abstract class Assignment {
    /**
     * Assignment ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Assignment deadline.
     */
    private Date deadline;

    /**
     * Assignment capacity.
     */
    private Integer capacity;

    /**
     * Min points you HAVE to get.
     */
    private Integer minPoints;

    /**
     * Max points you can get.
     */
    private Integer maxPoints;

    /**
     * Registered students for this assignment.
     */
    @ManyToMany
    private Set<Student> registeredStudents;

    /**
     * The supervisor for this assignment.
     */
    @ManyToOne
    private Teacher supervisor;

    /**
     * The course the assignment belongs to.
     */
    @ManyToOne
    private Course course;
}