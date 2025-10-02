package IIS.wis2_backend.Models;

import java.sql.Date;
import java.util.Set;

import IIS.wis2_backend.Enum.CourseEndType;
import IIS.wis2_backend.Enum.CourseType;
import IIS.wis2_backend.Models.User.Teacher;
import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing one course.
 */
@Entity
@Table(name = "COURSES")
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
    private String description;

    /**
     * Course shortcut/abbreviation.
     */
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
     * Course category.
     */
    @Enumerated(EnumType.STRING)
    private CourseType type;

    /**
     * Course end type.
     */
    @Enumerated(EnumType.STRING)
    private CourseEndType completedBy;

    /**
     * Course supervisor/"garant"
     */
    @ManyToOne
    private Teacher supervisor;

    /**
     * Course teachers
     */
    @ManyToMany
    private Set<Teacher> teachers;
}