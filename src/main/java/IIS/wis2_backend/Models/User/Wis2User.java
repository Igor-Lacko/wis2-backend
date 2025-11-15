package IIS.wis2_backend.Models.User;

import java.sql.Date;

import IIS.wis2_backend.Enum.Roles;
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
public class Wis2User {
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
     * User password hash.
     */
    @Column(nullable = false)
    private String password;

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

    /**
     * User role.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role;

    /**
     * If the user account is activated.
     */
    @Column(nullable = false)
    private boolean activated;

    /**
     * TODO: ak sa nam bude chciet byt fancy 
     * Last login
     */
}