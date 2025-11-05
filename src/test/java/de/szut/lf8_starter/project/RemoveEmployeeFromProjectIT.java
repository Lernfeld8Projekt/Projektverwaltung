package de.szut.lf8_starter.project;

import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RemoveEmployeeFromProjectIT extends AbstractIntegrationTest {
    @MockBean
    private EmployeeService employeeService;

    @Test
    void authorization() throws Exception {
        this.mockMvc.perform(post("/project/{projectID}", 1)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void removeEmployeeFromUnexistingProject_shouldThrowException() throws Exception {
        var projectEntity = new ProjectEntity();
        projectEntity.setTitle("BFK");
        projectEntity.setResponsibleEmployeeId(1L);
        projectEntity.setCustomerId(1L);
        projectEntity.setCustomerRepresentativeName("Max Meyer");
        projectEntity.setGoal("Project fertig machen");
        projectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        projectEntity.setPlannedEndDate(LocalDate.parse("2028-01-01"));
        projectRepository.save(projectEntity);

        Set<ProjectAssignment> projectAssignments = new HashSet<>();
        ProjectAssignment newProjectAssignment = new ProjectAssignment();
        newProjectAssignment.setEmployeeId(1L);
        newProjectAssignment.setQualificationId(1L);
        newProjectAssignment.setProject(projectEntity);
        projectAssignments.add(newProjectAssignment);
        projectEntity.setAssignments(projectAssignments);

        this.projectRepository.save(projectEntity);

        this.mockMvc.perform(delete("/project/{id}/{employeeId}", projectEntity.getId() + 1, 1L)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Project not found on id: " + (projectEntity.getId() + 1))));

    }

    @Test
    @WithMockUser(roles = "user")
    void removeUnexistingEmployeeFromProject_shouldThrowException() throws Exception {
        Mockito.doReturn(false).when(employeeService).checkIfEmployeeExists(222L);

        var projectEntity = new ProjectEntity();
        projectEntity.setTitle("BFK");
        projectEntity.setResponsibleEmployeeId(1L);
        projectEntity.setCustomerId(1L);
        projectEntity.setCustomerRepresentativeName("Max Meyer");
        projectEntity.setGoal("Project fertig machen");
        projectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        projectEntity.setPlannedEndDate(LocalDate.parse("2028-01-01"));
        projectRepository.save(projectEntity);

        Set<ProjectAssignment> projectAssignments = new HashSet<>();
        ProjectAssignment newProjectAssignment = new ProjectAssignment();
        newProjectAssignment.setEmployeeId(1L);
        newProjectAssignment.setQualificationId(1L);
        newProjectAssignment.setProject(projectEntity);
        projectAssignments.add(newProjectAssignment);
        projectEntity.setAssignments(projectAssignments);

        this.projectRepository.save(projectEntity);

        this.mockMvc.perform(delete("/project/{id}/{employeeId}", projectEntity.getId(), 222L)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Employee not found on id: 222")));
    }

    @Test
    @WithMockUser(roles = "user")
    void removeExistingEmployeeThatIsNotInTheProjectFromProject_shouldThrowException() throws Exception {
        Mockito.doReturn(true).when(employeeService).checkIfEmployeeExists(2L);

        var projectEntity = new ProjectEntity();
        projectEntity.setTitle("BFK");
        projectEntity.setResponsibleEmployeeId(1L);
        projectEntity.setCustomerId(1L);
        projectEntity.setCustomerRepresentativeName("Max Meyer");
        projectEntity.setGoal("Project fertig machen");
        projectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        projectEntity.setPlannedEndDate(LocalDate.parse("2028-01-01"));
        projectRepository.save(projectEntity);

        Set<ProjectAssignment> projectAssignments = new HashSet<>();
        ProjectAssignment newProjectAssignment = new ProjectAssignment();
        newProjectAssignment.setEmployeeId(1L);
        newProjectAssignment.setQualificationId(1L);
        newProjectAssignment.setProject(projectEntity);
        projectAssignments.add(newProjectAssignment);
        projectEntity.setAssignments(projectAssignments);

        this.projectRepository.save(projectEntity);

        this.mockMvc.perform(delete("/project/{id}/{employeeId}", projectEntity.getId(), 2L)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("The Employee with ID " + 2 + " is not a part of the project!")));
    }

    @Test
    @WithMockUser(roles = "user")
    void removeExistingEmployeeFromProject() throws Exception {
        Mockito.doReturn(true).when(employeeService).checkIfEmployeeExists(1L);

        var projectEntity = new ProjectEntity();
        projectEntity.setTitle("BFK");
        projectEntity.setResponsibleEmployeeId(1L);
        projectEntity.setCustomerId(1L);
        projectEntity.setCustomerRepresentativeName("Max Meyer");
        projectEntity.setGoal("Project fertig machen");
        projectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        projectEntity.setPlannedEndDate(LocalDate.parse("2028-01-01"));
        projectRepository.save(projectEntity);

        Set<ProjectAssignment> projectAssignments = new HashSet<>();
        ProjectAssignment newProjectAssignment = new ProjectAssignment();
        newProjectAssignment.setEmployeeId(1L);
        newProjectAssignment.setQualificationId(1L);
        newProjectAssignment.setProject(projectEntity);
        projectAssignments.add(newProjectAssignment);
        projectEntity.setAssignments(projectAssignments);

        this.projectRepository.save(projectEntity);

        final var contentAsString = this.mockMvc.perform(delete("/project/{id}/{employeeId}", projectEntity.getId(), 1L)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}