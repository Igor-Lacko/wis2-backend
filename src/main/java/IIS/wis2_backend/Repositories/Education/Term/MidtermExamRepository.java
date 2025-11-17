package IIS.wis2_backend.Repositories.Education.Term;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.Term.MidtermExam;

/**
 * Repository for midterm exams.
 */
@Repository
public interface MidtermExamRepository extends JpaRepository<MidtermExam, Long> {
}