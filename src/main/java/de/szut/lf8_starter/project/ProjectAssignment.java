package de.szut.lf8_starter.project;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "project_assignment")
public class ProjectAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    private Long employeeId;
    private Long qualificationId;
}
