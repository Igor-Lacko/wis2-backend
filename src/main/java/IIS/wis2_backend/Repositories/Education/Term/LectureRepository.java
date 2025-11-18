package IIS.wis2_backend.Repositories.Education.Term;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import IIS.wis2_backend.Models.Term.Lecture;

/**
 * Repository for lecture terms.
 */
@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
}
