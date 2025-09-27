package IIS.wis2_backend.Models.User;

import jakarta.persistence.*;

/**
 * Model representing a teacher. Inherits from User.
 */
@Entity
@Table(name = "TEACHERS")
public class Teacher extends User {

}