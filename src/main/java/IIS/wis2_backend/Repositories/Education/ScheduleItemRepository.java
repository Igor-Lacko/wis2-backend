package IIS.wis2_backend.Repositories.Education;

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

}
