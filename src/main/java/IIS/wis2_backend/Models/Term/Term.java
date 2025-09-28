package IIS.wis2_backend.Models.Term;

import java.util.Set;

import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Room.Room;
import IIS.wis2_backend.Models.User.Student;
import IIS.wis2_backend.Models.User.Teacher;
import jakarta.persistence.*;

/**
 * Base model for all term types.
 */
@Entity
@Table(name = "TERMS")
@Inheritance(strategy = InheritanceType.JOINED)
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
     * Term description.
     */
    private String description;

    /**
     * Term name.
     */
    private String name;

    /**
     * Indicates if the term is mandatory.
     */
    private Boolean mandatory;

    /**
     * Registered students for this term.
     */
    @OneToMany
    private Set<Student> students;

    /**
     * Teacher for this term.
     */
    @ManyToOne
    private Teacher supervisor;

    /**
     * Room or rooms where the term takes place.
     */
    @ManyToMany
    private Set<Room> rooms;

    /**
     * Course this term belongs to.
     */
    @ManyToOne
    private Course course;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public Teacher getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Teacher supervisor) {
        this.supervisor = supervisor;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    
}