package IIS.wis2_backend.Models.Room;

import java.util.Set;

import IIS.wis2_backend.Models.User.Teacher;
import jakarta.persistence.*;

/**
 * Model representing a office of academic workers.
 */
@Entity
@Table(name = "OFFICES")
public class Office extends Room {
    /**
     * Teachers whose office this is.
     */
    @OneToMany(mappedBy = "office")
    private Set<Teacher> teachers;

    public Set<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(Set<Teacher> teachers) {
        this.teachers = teachers;
    }
}