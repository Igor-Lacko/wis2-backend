package IIS.wis2_backend.Models.Term;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Relational.StudentTerm;
import IIS.wis2_backend.Models.Room.Room;
import IIS.wis2_backend.Models.User.Wis2User;
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
    private Integer minPoints;

    /**
     * Max amount of points you CAN receive for the term.
     */
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
    @Builder.Default
    private Set<StudentTerm> studentTerms = new HashSet<StudentTerm>();

    /**
     * Teacher for this term.
     */
    @ManyToOne
    @JoinColumn(nullable = false, name = "teacher_id")
    private Wis2User supervisor;

    /**
     * Room or rooms where the term takes place.
     */
    @ManyToOne
    @JoinColumn(nullable = false, name = "room_id")
    private Room room;

    /**
     * Course this term belongs to.
     */
    @ManyToOne
    @JoinColumn(nullable = false, name = "course_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Course course;

    /**
     * If the term is autoregistered.
     */
    @Builder.Default
    private Boolean autoregistered = false;
}