package IIS.wis2_backend.Models;

import IIS.wis2_backend.Models.Room.Room;
import IIS.wis2_backend.Models.User.User;
import jakarta.persistence.*;

/**
 * Model representing one schedule.
 */
@Entity
@Table(name = "SCHEDULE")
public class Schedule {
    /**
     * Schedule ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Schedule user.
     */
    @OneToOne(optional = true)
    private User user;

    /**
     * Or schedule room!
     */
    @OneToOne(optional = true)
    private Room room;

    /**
     * Or even... schedule course?
     */
    @OneToOne(optional = true)
    private Course course;

    protected Schedule() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}