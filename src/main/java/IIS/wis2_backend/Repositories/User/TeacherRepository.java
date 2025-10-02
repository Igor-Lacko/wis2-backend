package IIS.wis2_backend.Repositories.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import IIS.wis2_backend.Models.User.Teacher;

/**
 * Repository for teacher specific CRUD operations.
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    /**
     * Find teachers by first or last name containing the given string..
     */
    List<Teacher> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}