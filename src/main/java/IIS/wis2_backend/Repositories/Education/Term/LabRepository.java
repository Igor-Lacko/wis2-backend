package IIS.wis2_backend.Repositories.Education.Term;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import IIS.wis2_backend.Models.Term.Lab;

/**
 * Repository for lab terms.
 */
@Repository
public interface LabRepository extends JpaRepository<Lab, Long> {
}