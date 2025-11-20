package IIS.wis2_backend.Models.Room;

import java.util.Set;

import IIS.wis2_backend.Models.User.Wis2User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Model representing a office of academic workers.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Office extends Room {
    /**
     * Teachers whose office this is.
     */
    @OneToMany(mappedBy = "office")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Wis2User> teachers;
}