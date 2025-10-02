package IIS.wis2_backend.Models.User;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Model representing a registered user.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@SuperBuilder
public class User {
    /**
     * User ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User first name.
     */
    String firstName;

    /**
     * User last name.
     */
    String lastName;

    /**
     * User email.
     */
    String email;

    /**
     * User birthday.
     */
    Date birthday;

    /**
     * User tel. number (todo: more appropriate data type?)
     */
    String telephoneNumber;
}