package IIS.wis2_backend.Models;

import java.sql.Date;
import java.util.Set;

import IIS.wis2_backend.Enum.CourseEndType;
import IIS.wis2_backend.Models.Lesson.Lesson;
import IIS.wis2_backend.Models.Relational.StudentCourse;
import IIS.wis2_backend.Models.Term.Term;
import IIS.wis2_backend.Models.User.Teacher;
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
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Course shortcut/abbreviation.
     */
    @Column(unique = true, nullable = false)
    private String shortcut;

    /**
     * Date the course was created.
     */
    @Column(nullable = false)
    private Date createdAt;

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
    @Column(nullable = false)
    private Teacher supervisor;

    /**
     * Course teachers
     */
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Teacher> teachers;

    /**
     * Relation to students.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentCourse> studentCourses;

    /**
     * Relation to terms.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Term> terms;

    /**
     * And to lessons!
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Lesson> lessons;

    /**
     * Course schedule
     */
    @OneToOne
    private Schedule schedule;

    /**
     * Course capacity
     */
    @Column(nullable = false)
    private Integer capacity;
}