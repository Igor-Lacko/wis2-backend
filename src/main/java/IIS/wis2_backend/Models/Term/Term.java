package IIS.wis2_backend.Models.Term;

import java.time.LocalDateTime;
import java.util.Set;

import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Relational.StudentTerm;
import IIS.wis2_backend.Models.Room.Room;
import IIS.wis2_backend.Models.User.Teacher;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Base model for all term types.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
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
    @Column(nullable = false)
    private Integer minPoints;

    /**
     * Max amount of points you CAN receive for the term.
     */
    @Column(nullable = false)
    private Integer maxPoints;

    /**
     * Term date.
     */
    @Column(nullable = false)
    private LocalDateTime date;

    /**
     * Term duration in minutes.
     */
    @Column(nullable = false)
    private Integer duration;

    /**
     * Term description.
     */
    private String description;

    /**
     * Term name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Registered students for this term.
     */
    @OneToMany
    private Set<StudentTerm> students;

    /**
     * Teacher for this term.
     */
    @ManyToOne
    @Column(nullable = false)
    private Teacher supervisor;

    /**
     * Room or rooms where the term takes place.
     */
    @ManyToOne
    @Column(nullable = false)
    private Room room;

    /**
     * Course this term belongs to.
     */
    @ManyToOne
    @Column(nullable = false)
    private Course course;
}