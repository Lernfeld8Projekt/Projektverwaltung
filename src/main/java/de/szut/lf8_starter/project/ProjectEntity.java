package de.szut.lf8_starter.project;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "project")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Long responsibleEmployeeId;

    private Long customerId;

    private String customerRepresentativeName;

    private String goal;

    private LocalDate startDate;

    private LocalDate plannedEndDate;

    private LocalDate actualEndDate = null;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ProjectAssignment> assignments = new HashSet<>();

}
