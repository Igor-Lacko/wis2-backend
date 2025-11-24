package IIS.wis2_backend.Models.User;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringExclude;

import IIS.wis2_backend.Enum.Roles;
import IIS.wis2_backend.Models.Course;
import IIS.wis2_backend.Models.Schedule;
import IIS.wis2_backend.Models.Relational.StudentCourse;
import IIS.wis2_backend.Models.Relational.StudentTerm;
import IIS.wis2_backend.Models.Room.Office;
import IIS.wis2_backend.Models.Tokens.RefreshToken;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Model representing a registered user.
 */
@Entity
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
    @Builder.Default
    private Roles role = Roles.USER;

    /**
     * If the user account is activated.
     */
    @Builder.Default
    private boolean activated = false;

    /**
     * User schedule as a set of schedule items.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    /**
     * User refresh token.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    // --- Student Fields ---
    /**
     * Relation to courses.
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<StudentCourse> studentCourses;

    /**
     * Relation to terms.
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<StudentTerm> studentTerms;

    // --- Teacher Fields ---

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
    @Builder.Default
    private Set<Course> supervisedCourses = new HashSet<>();

    /**
     * Taught courses.
     */
    @ManyToMany(mappedBy = "teachers", fetch = FetchType.LAZY)
    @ToStringExclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Course> taughtCourses = new HashSet<>();
}