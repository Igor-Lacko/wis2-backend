package IIS.wis2_backend.Repositories.Education.Lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.Lesson.Lesson;

/**
 * Repository for lessons.
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
}