package IIS.wis2_backend.DTO.Response.Room;

import java.util.List;

import IIS.wis2_backend.Enum.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDetailsDTO {
    private Long id;
    private String shortcut;
    private String building;
    private String floor;
    private Integer capacity;
    private RoomType roomType;
    private List<Long> occupantUserIds;
}
