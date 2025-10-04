package IIS.wis2_backend.Models.User;

import IIS.wis2_backend.Models.Room.Office;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Model representing a teacher. Inherits from WIS2User.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Teacher extends WIS2User {
    /**
     * Teacher's office.
     */
    @ManyToOne
    private Office office;
}