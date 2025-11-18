package IIS.wis2_backend.Repositories.Room;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.Room.Room;

/**
 * Repository for rooms.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    /**
     * Find a room by its shortcut.
     * 
     * @param shortcut the shortcut of the room
     * @return an Optional containing the room if found, or empty if not found
     */
    Optional<Room> findByShortcut(String shortcut);
}