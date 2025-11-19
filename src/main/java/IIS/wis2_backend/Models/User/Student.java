package IIS.wis2_backend.Models.User;

import java.util.Set;

import IIS.wis2_backend.Models.Relational.StudentCourse;
import IIS.wis2_backend.Models.Relational.StudentTerm;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
/**
 * Model representing one student. Inherits from WIS2User.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Student extends Wis2User {
    /**
     * Student's GPA
     */
    Float gradeAverage;

    /**
     * Relation to courses.
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentCourse> studentCourses;

    /**
     * Relation to terms.
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentTerm> studentTerms;
}