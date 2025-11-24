package IIS.wis2_backend.Repositories.Education.Term;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.Term.Term;

/**
 * Repository for terms.
 */
@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    /**
     * Gets the term's capacity by its ID.
     * 
     * @param id the ID of the term
     */
    @Query("SELECT t.room.capacity FROM Term t WHERE t.id = :id")
    Integer getTermCapacityById(Long id);
}