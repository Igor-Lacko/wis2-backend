package IIS.wis2_backend.Repositories.Education.Schedule;

import java.util.Optional;

import IIS.wis2_backend.Models.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository for schedule items.
 */
@Repository
public interface ScheduleItemRepository
                extends JpaRepository<ScheduleItem, Long>, JpaSpecificationExecutor<ScheduleItem> {
    
    Optional<ScheduleItem> findByTerm_Id(Long termId);

}
