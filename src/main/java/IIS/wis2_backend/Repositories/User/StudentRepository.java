package IIS.wis2_backend.Repositories.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.User.Student;

/**
 * Repository for student specific CRUD operations.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    /**
     * Find student by name.
     */
    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}