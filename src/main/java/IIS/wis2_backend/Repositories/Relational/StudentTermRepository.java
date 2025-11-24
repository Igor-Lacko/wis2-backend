package IIS.wis2_backend.Repositories.Relational;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import IIS.wis2_backend.Models.Relational.StudentTerm;

@Repository
public interface StudentTermRepository extends JpaRepository<StudentTerm, Long> {
    Optional<StudentTerm> findByTermIdAndStudentId(Long termId, Long studentId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM StudentTerm st WHERE st.term.id = :termId AND st.student.id = :studentId")
    void deleteByTermIdAndStudentId(Long termId, Long studentId);

    /**
     * Returns the count of students registered for a given term.
     * 
     * @param termId the ID of the term
     */
    long countByTermId(Long termId);
}
