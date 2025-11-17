package IIS.wis2_backend.Repositories.Education.Lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import IIS.wis2_backend.Models.Lesson.Lecture;

/**
 * Repository for Lecture entities.
 */
@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
}