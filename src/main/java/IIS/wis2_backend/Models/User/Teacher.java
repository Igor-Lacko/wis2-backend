package IIS.wis2_backend.Models.User;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringExclude;

import IIS.wis2_backend.Models.Course;
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
public class Teacher extends Wis2User {
    /**
     * Teacher's office.
     */
    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    /**
     * Supervised courses.
     */
    @OneToMany(mappedBy = "supervisor", fetch = FetchType.LAZY)
    @ToStringExclude
    @EqualsAndHashCode.Exclude
    private Set<Course> supervisedCourses;

    /**
     * Taught courses.
     */
    @ManyToMany(mappedBy = "teachers", fetch = FetchType.LAZY)
    @ToStringExclude
    @EqualsAndHashCode.Exclude
    private Set<Course> taughtCourses;
}