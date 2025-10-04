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
     * Unique username.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * User first name.
     */
    @Column(nullable = false)
    private String firstName;

    /**
     * User last name.
     */
    @Column(nullable = false)
    private String lastName;

    /**
     * User email.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * User birthday.
     */
    @Column(nullable = false)
    private Date birthday;

    /**
     * User tel. number (todo: more appropriate data type?)
     */
    @Column(nullable = true, unique = true)
    private String telephoneNumber;
}