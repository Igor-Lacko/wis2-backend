package IIS.wis2_backend.Models.User;

import jakarta.persistence.*;

/**
 * Model representing one student. Inherits from User.
 */
@Entity
@Table(name = "STUDENTS")
public class Student extends User {
    /**
     * Student's GPA
     */
    Float gradeAverage;
}