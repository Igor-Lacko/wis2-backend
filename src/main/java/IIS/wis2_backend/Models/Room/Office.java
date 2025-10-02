package IIS.wis2_backend.Models.Room;

import java.util.Set;

import IIS.wis2_backend.Models.User.Teacher;
import jakarta.persistence.*;
import lombok.*;

/**
 * Model representing a office of academic workers.
 */
@Entity
@Table(name = "OFFICES")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Office extends Room {
    /**
     * Teachers whose office this is.
     */
    @OneToMany(mappedBy = "office")
    private Set<Teacher> teachers;
}