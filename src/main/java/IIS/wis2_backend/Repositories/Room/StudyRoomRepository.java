package IIS.wis2_backend.Repositories.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.DTO.Response.Room.AvailableRoomDTO;
import IIS.wis2_backend.Models.Room.StudyRoom;

/**
 * Repository for study room CRUD operations.
 */
@Repository
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {
	/**
	 * Finds available rooms for the given interval.
	 * 
	 * @para
	 */
	@Query("SELECT new IIS.wis2_backend.DTO.Response.Room.AvailableRoomDTO(r.shortcut, r.building, r.floor) " +
			"FROM StudyRoom r " +
			"WHERE r.id NOT IN ( " +
			"   SELECT t.room.id FROM Term t " +
			"   WHERE (t.date < :end AND t.endDate > :start) " +
			")")
	public List<AvailableRoomDTO> findAvailableRoomDTOs(@Param("start") LocalDateTime date,
			@Param("end") LocalDateTime endDate);

	List<StudyRoom> findByShortcut(String shortcut);

	Optional<StudyRoom> findMaybeByShortcut(String shortcut);

	@Query("SELECT CASE WHEN COUNT(r) = 0 THEN TRUE ELSE FALSE END " +
			"FROM StudyRoom r " +
			"WHERE r.shortcut = :shortcut AND r.id IN ( " +
			"   SELECT t.room.id FROM Term t " +
			"   WHERE (t.date < :end AND t.endDate > :start) " +
			")")
	public Boolean isAvaliableBetween(@Param("shortcut") String shortcut, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);
}
