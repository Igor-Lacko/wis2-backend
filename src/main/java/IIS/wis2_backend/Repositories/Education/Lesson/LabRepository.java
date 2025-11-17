package IIS.wis2_backend.Repositories.Education.Lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import IIS.wis2_backend.Models.Lesson.Lab;

/**
 * Repository for Lab entities.
 */
@Repository
public interface LabRepository extends JpaRepository<Lab, Long> {
}