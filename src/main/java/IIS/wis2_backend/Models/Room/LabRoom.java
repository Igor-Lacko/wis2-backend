package IIS.wis2_backend.Models.Room;

import jakarta.persistence.*;

/**
 * Model representing a lab room.
 */
@Entity
@Table(name = "LAB_ROOMS")
public class LabRoom extends Room {
    /**
     * If the room has access to computers or not.
     */
    private Boolean pcSupport;

    public Boolean getPcSupport() {
        return pcSupport;
    }

    public void setPcSupport(Boolean pcSupport) {
        this.pcSupport = pcSupport;
    }
}