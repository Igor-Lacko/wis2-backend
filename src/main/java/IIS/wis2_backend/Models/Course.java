package IIS.wis2_backend.Models;

import java.util.Set;
import java.util.HashSet;

import IIS.wis2_backend.Enum.CourseEndType;
import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Models.Relational.StudentCourse;
import IIS.wis2_backend.Models.Term.Term;
import IIS.wis2_backend.Models.User.Wis2User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing one course.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    /**
     * Course ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Course name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Course price. (keep this here?)
     */
    @Column(nullable = false)
    private Double price;

    /**
     * Course description.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Course shortcut/abbreviation.
     */
    @Column(unique = true, nullable = false)
    private String shortcut;

    /**
     * Course end type.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseEndType completedBy;

    /**
     * Course supervisor/"garant"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "supervisor_id")
    private Wis2User supervisor;

    /**
     * Course teachers
     */
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Wis2User> teachers;

    /**
     * Relation to students.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<StudentCourse> studentCourses = new HashSet<StudentCourse>();

    /**
     * Relation to terms.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Term> terms = new HashSet<Term>();

    /**
     * Course schedule.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    /**
     * Course capacity
     */
    @Column(nullable = false)
    private Integer capacity;

    /**
     * If the course can be autoregistered up to capacity.
     */
    @Column(nullable = false)
    private Boolean autoregister;

    /**
     * Course approval status.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;
}