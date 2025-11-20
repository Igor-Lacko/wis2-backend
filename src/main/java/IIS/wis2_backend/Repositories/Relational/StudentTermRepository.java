package IIS.wis2_backend.Repositories.Relational;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.Relational.StudentTerm;

@Repository
public interface StudentTermRepository extends JpaRepository<StudentTerm, Long> {
    Optional<StudentTerm> findByTermIdAndStudentId(Long termId, Long studentId);
}
