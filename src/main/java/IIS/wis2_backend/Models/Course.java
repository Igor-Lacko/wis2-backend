package IIS.wis2_backend.Models;

import java.sql.Date;
import java.util.Set;

import IIS.wis2_backend.Enum.CourseEndType;
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
    private String name;

    /**
     * Course price. (keep this here?)
     */
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
    private Date createdAt;

    /**
     * Date the course was last updated.
     */
    private Date updatedAt;

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
    private Teacher supervisor;

    /**
     * Course teachers
     */
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Teacher> teachers;

    /**
     * Course schedule
     */
    @OneToOne
    private Schedule schedule;
}