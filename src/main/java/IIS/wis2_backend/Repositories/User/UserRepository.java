package IIS.wis2_backend.Repositories.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.User.User;

/**
 * Repository for shared user CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds users by name containing the given string.
     */
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}