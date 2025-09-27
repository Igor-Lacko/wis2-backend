package IIS.wis2_backend.Models;

import java.sql.Date;
import IIS.wis2_backend.Enum.CourseEndType;
import IIS.wis2_backend.Enum.CourseType;
import jakarta.persistence.*;;

/**
 * Model representing one course.
 */
@Entity
@Table(name = "COURSES")
class Course {
    /**
     * Course ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    /**
     * Course name.
     */
    String name;

    /**
     * Course price. (keep this here?)
     */
    Double price;

    /**
     * Course description.
     */
    String description;

    /**
     * Course shortcut/abbreviation.
     */
    String shortcut;

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
    CourseType type;

    /**
     * Course end type.
     */
    @Enumerated(EnumType.STRING)
    CourseEndType completedBy;
}