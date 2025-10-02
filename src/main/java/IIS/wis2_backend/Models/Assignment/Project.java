package IIS.wis2_backend.Models.Assignment;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

/**
 * Model representing a project.
 */
@Entity
@Table(name = "PROJECTS")
@NoArgsConstructor
public class Project extends Assignment {
}