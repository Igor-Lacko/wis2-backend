package IIS.wis2_backend.Models.Assignment;

import java.sql.Date;
import java.util.Set;

import IIS.wis2_backend.Models.User.*;
import IIS.wis2_backend.Models.Course;

import jakarta.persistence.*;

/**
 * Abstract model representing an assignment (e.g. projects, homework).
 */
@Entity
@Table(name = "ASSIGNMENTS")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Assignment {
    /**
     * Assignment ID.
     */
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(Integer minPoints) {
        this.minPoints = minPoints;
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }

    public Set<Student> getRegisteredStudents() {
        return registeredStudents;
    }

    public void setRegisteredStudents(Set<Student> registeredStudents) {
        this.registeredStudents = registeredStudents;
    }

    public Teacher getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Teacher supervisor) {
        this.supervisor = supervisor;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}