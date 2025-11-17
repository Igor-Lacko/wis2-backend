package IIS.wis2_backend.Repositories.Education.Term;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.Term.Term;

/**
 * Repository for terms.
 */
@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    
}