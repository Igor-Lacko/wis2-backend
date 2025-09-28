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

    protected User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
}