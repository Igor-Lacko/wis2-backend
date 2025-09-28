package IIS.wis2_backend.Models.Lesson;

import java.sql.Date;
import java.util.Set;

import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.User.Student;
import IIS.wis2_backend.Models.User.Teacher;
import jakarta.persistence.*;

/**
 * Model representing an abstract lesson (e.g. lectures or labs)
 */
@Entity
@Table(name = "LESSONS")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Lesson {
    /**
     * Lesson ID.
     */
    private Long id;

    /**
     * Lesson datetime.
     */
    private Date datetime;

    /**
     * Lesson duration in minutes.
     */
    private Integer duration;

    /**
     * Students registered for the lesson.
     */
    @OneToMany
    private Set<Student> registeredStudents;

    /**
     * The teacher for this lesson.
     */
    @ManyToOne
    private Teacher supervisor;

    /**
     * The course this lesson belongs to.
     */
    @ManyToOne
    private Course course;

    protected Lesson() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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