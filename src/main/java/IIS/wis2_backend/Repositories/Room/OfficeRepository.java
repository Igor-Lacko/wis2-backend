package IIS.wis2_backend.Repositories.Room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IIS.wis2_backend.Models.Room.Office;

/**
 * Repository interface for Office entity.
 */
@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {
    /**
     * Checks if an office with the given shortcut exists.
     * 
     * @param shortcut the shortcut to check
     * @return true if an office with the given shortcut exists, false otherwise
     */
    boolean existsByShortcut(String shortcut);

    /**
     * Finds an office by its shortcut.
     * 
     * @param shortcut the shortcut of the office
     * @return the Office entity with the given shortcut
     */
    Office findByShortcut(String shortcut);
}