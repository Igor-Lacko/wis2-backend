package IIS.wis2_backend.Specifications;

import java.sql.Date;

import org.springframework.data.jpa.domain.Specification;

import IIS.wis2_backend.Models.ScheduleItem;

/**
 * Specification class for ScheduleItem entity.
 */
public class ScheduleItemSpecification {
    /**
     * Specification to filter ScheduleItems by schedule ID and week date range.
     * 
     * @param scheduleId    ID of the schedule.
     * @param weekStartDate Start date of the week.
     * @return Specification for ScheduleItem.
     */
    public static Specification<ScheduleItem> HasScheduleIdAndIsInGivenWeek(long scheduleId, Date weekStartDate) {
        Date weekEndDate = new Date(weekStartDate.getTime() + 7 * 24 * 60 * 60 * 1000);
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("schedule").get("id"), scheduleId),
                criteriaBuilder.between(root.get("date"), weekStartDate, weekEndDate));
    }
}