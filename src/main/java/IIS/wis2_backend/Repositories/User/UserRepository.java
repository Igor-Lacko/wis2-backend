package IIS.wis2_backend.Repositories.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
     * Finds a user by username.
     * 
     * @param username Username to search for.
     * @return Wis2User with the given username, or null if not found.
     */
    Wis2User findByUsername(String username);
}