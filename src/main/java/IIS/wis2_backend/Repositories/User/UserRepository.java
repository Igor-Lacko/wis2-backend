package IIS.wis2_backend.Repositories.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.User.WIS2User;

/**
 * Repository for shared user CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<WIS2User, Long> {
    /**
     * Finds users by name containing the given string.
     */
    List<WIS2User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    /**
     * Checks if a user with the given username (case insensitive) or email exists.
     */
    boolean existsByUsernameIgnoreCaseOrEmail(String username, String email);

    /**
     * Checks only by email.
     */
    boolean existsByEmail(String email);
}