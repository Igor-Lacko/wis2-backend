package IIS.wis2_backend.Models.Assignment;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

/**
 * Model representing home work. Todo: make them one with the project if they will be too similiar?
 */
@Entity
@Table(name = "HOMEWORK")
@NoArgsConstructor
public class Homework extends Assignment {
}