package IIS.wis2_backend.Repositories.User;

import java.util.Set;

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
     * 
     * @param firstName First name to search for.
     * @param lastName  Last name to search for.
     */
    Set<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    /**
     * Finds all students in a given course by using StudentCourse relationship.
     * 
     * @param courseId the ID of the course
     * @return list of students enrolled in the course
     */
    Set<Student> findByStudentCourses_Course_Id(Long courseId);
}