package IIS.wis2_backend.Repositories.Education.Term;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import IIS.wis2_backend.Models.Term.Exam;

/**
 * Repository for exams.
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
}