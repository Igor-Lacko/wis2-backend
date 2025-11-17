package IIS.wis2_backend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.Room.Room;

/**
 * Repository for rooms.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}