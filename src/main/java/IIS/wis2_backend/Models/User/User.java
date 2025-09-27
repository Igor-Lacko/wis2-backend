package IIS.wis2_backend.Models.User;

import java.sql.Date;

import jakarta.persistence.*;

/**
 * Model representing an abstract user. This isn't actually ever used.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
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