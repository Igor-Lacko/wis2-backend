package IIS.wis2_backend.Models.Room;

import jakarta.persistence.*;

/**
 * Abstract model representing one room on the campus.
 */
@Entity
@Table(name = "ROOMS")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Room {
    /**
     * Room identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Room "tag/shortcut" (todo: maybe make this the Id? idk tbh)
     */
    private String shortcut;

    /**
     * Room building.
     */
    private String building;

    /**
     * Room floor. Is a string ("basement, etc.")
     */
    private String floor;

    /**
     * Room capacity.
     */
    private Integer capacity;

    protected Room() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}