package IIS.wis2_backend.Models.User;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ADMINS")
@NoArgsConstructor
public class Admin extends User {
}