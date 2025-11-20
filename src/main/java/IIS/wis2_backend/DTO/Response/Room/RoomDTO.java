package IIS.wis2_backend.DTO.Response.Room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomDTO {
    private Long id;
    private String name;
    private Integer capacity;
    private String building;
    private String floor;
}
