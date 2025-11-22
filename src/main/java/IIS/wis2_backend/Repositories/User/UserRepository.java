package IIS.wis2_backend.Repositories.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Enum.RequestStatus;
import IIS.wis2_backend.Enum.Roles;
import IIS.wis2_backend.Models.User.Wis2User;

/**
 * Repository for shared user CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<Wis2User, Long> {
    /**
     * Finds users by name containing the given string.
     * 
     * @param firstName First name to search for.
     * @param lastName  Last name to search for.
     */
    List<Wis2User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    /**
     * Checks if a user with the given username (case insensitive) or email exists.
     * 
     * @param username Username to check.
     * @param email    Email to check.
     */
    boolean existsByUsernameIgnoreCaseOrEmail(String username, String email);

    /**
     * Checks only by email.
     * 
     * @param email Email to check.
     * @return true if a user with the given email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Checks only by username.
     * 
     * @param username Username to check.
     * @return True if a user with the given username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks only by telephone number.
     * 
     * @param telephoneNumber Telephone number to check.
     * @return True if a user with the given telephone number exists, false otherwise.
     */
    boolean existsByTelephoneNumber(String telephoneNumber);

    /**
     * Finds a user by username.
     * 
     * @param username Username to search for.
     * @return Optional containing the Wis2User with the given username, or empty if not found.
     */
    Optional<Wis2User> findByUsername(String username);

    /**
     * Finds a user by email.
     * 
     * @param email Email to search for.
     * @return Optional containing the Wis2User with the given email, or empty if not found.
     */
    Optional<Wis2User> findByEmail(String email);

    /**
     * Finds all students in a given course by using StudentCourse relationship.
     * 
     * @param courseId the ID of the course
     * @param status the status of the enrollment
     * @return list of students enrolled in the course
     */
    Set<Wis2User> findByStudentCourses_Course_IdAndStudentCourses_Status(Long courseId, RequestStatus status);

    /**
     * Finds all student registered for a given term by using StudentTerm relationship.
     * 
     * @param termId the ID of the term
     * @return list of students registered for the term
     */
    Set<Wis2User> findByStudentTerms_Term_Id(Long termId);

    /**
     * Finds all users currently assigned to the given office.
     *
     * @param officeId the office identifier
     * @return list of users occupying the office
     */
    List<Wis2User> findAllByOffice_Id(Long officeId);

    /**
     * Find the course's supervising teacher.
     * 
     * @param courseId ID of the course.
     * @return The supervising teacher of the course.
     */
    Wis2User findTopBySupervisedCourses_Id(Long courseId);

    /**
     * Find teachers teaching a specific course.
     * 
     * @param courseId ID of the course.
     * 
     * @return List of teachers teaching the course.
     */
    List<Wis2User> findAllByTaughtCourses_Id(Long courseId);

    /**
     * Checks if the user teaches the course with the given shortcut.
     * 
     * @param username Username of the user.
     * @param shortcut Shortcut of the course.
     * @return true if the user teaches the course, false otherwise.
     */
    boolean existsByUsernameAndTaughtCourses_Shortcut(String username, String shortcut);

    /**
     * Checks if the user studies the course with the given shortcut.
     * 
     * @param username Username of the user.
     * @param shortcut Shortcut of the course.
     * @param status Status of the enrollment.
     * @return true if the user studies the course, false otherwise.
     */
    boolean existsByUsernameAndStudentCourses_Course_ShortcutAndStudentCourses_Status(String username, String shortcut, RequestStatus status);

    /**
     * Returns all users with the provided role.
     *
     * @param role target role
     * @return list of matching users
     */
    List<Wis2User> findAllByRole(Roles role);

    /**
     * Returns all users with the provided role who do not have an assigned office.
     *
     * @param role target role
     * @return list of matching users without offices
     */
    List<Wis2User> findAllByRoleAndOfficeIsNull(Roles role);
}