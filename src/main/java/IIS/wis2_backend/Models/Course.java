package IIS.wis2_backend.Models;

import java.sql.Date;
import java.util.Set;

import IIS.wis2_backend.Enum.CourseEndType;
import IIS.wis2_backend.Enum.CourseType;
import IIS.wis2_backend.Models.User.Teacher;
import jakarta.persistence.*;;

/**
 * Model representing one course.
 */
@Entity
@Table(name = "COURSES")
public class Course {
    /**
     * Course ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    protected Course() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CourseType getType() {
        return type;
    }

    public void setType(CourseType type) {
        this.type = type;
    }

    public CourseEndType getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(CourseEndType completedBy) {
        this.completedBy = completedBy;
    }

    public Teacher getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Teacher supervisor) {
        this.supervisor = supervisor;
    }

    public Set<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(Set<Teacher> teachers) {
        this.teachers = teachers;
    }

    
}