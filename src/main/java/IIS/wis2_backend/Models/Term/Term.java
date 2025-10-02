package IIS.wis2_backend.Models.Term;

import java.util.Set;

import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Room.Room;
import IIS.wis2_backend.Models.User.Student;
import IIS.wis2_backend.Models.User.Teacher;
import jakarta.persistence.*;
import lombok.*;

/**
 * Base model for all term types.
 */
@Entity
@Table(name = "TERMS")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Term {
    /**
     * Term ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Min amount of points you HAVE to receive for the term. (Well, can be 0)
     */
    private Integer minPoints;

    /**
     * Max amount of points you CAN receive for the term.
     */
    private Integer maxPoints;

    /**
     * Term description.
     */
    private String description;

    /**
     * Term name.
     */
    private String name;

    /**
     * Indicates if the term is mandatory.
     */
    private Boolean mandatory;

    /**
     * Registered students for this term.
     */
    @OneToMany
    private Set<Student> students;

    /**
     * Teacher for this term.
     */
    @ManyToOne
    private Teacher supervisor;

    /**
     * Room or rooms where the term takes place.
     */
    @ManyToMany
    private Set<Room> rooms;

    /**
     * Course this term belongs to.
     */
    @ManyToOne
    private Course course;
}