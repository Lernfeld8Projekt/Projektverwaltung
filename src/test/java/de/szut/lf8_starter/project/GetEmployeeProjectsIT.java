package de.szut.lf8_starter.project;

import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

public class GetEmployeeProjectsIT extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void authorization() throws Exception {
        this.mockMvc.perform(get("/project/employee/{employeeId}", 1)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void getProjectsForEmployee() throws Exception {
        Mockito.doReturn(true).when(employeeService).checkIfEmployeeExists(5L);
        Mockito.doReturn(
                Map.of(
                        "firstName", "John",
                        "lastName", "Doe",
                        "skillSet", List.of(
                                Map.of("skill", "Java"),
                                Map.of("skill", "Spring")
                        )
                )
        ).when(employeeService).getEmployeeById(5L);

        ProjectEntity project = new ProjectEntity();
        project.setTitle("Employee Project");
        project.setResponsibleEmployeeId(5L);
        project.setCustomerId(10L);
        project.setCustomerRepresentativeName("Klaus Kundin");
        project.setGoal("Test Goal");
        project.setStartDate(LocalDate.parse("2024-01-01"));
        project.setPlannedEndDate(LocalDate.parse("2024-12-31"));
        projectRepository.save(project);

        ProjectAssignment assignment = new ProjectAssignment();
        assignment.setEmployeeId(5L);
        assignment.setProject(project);
        project.getAssignments().add(assignment);
        projectRepository.save(project);

        this.mockMvc.perform(get("/project/employee/{employeeId}", 5)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(5)))
                .andExpect(jsonPath("$.employeeName", is("John Doe")))
                .andExpect(jsonPath("$.skillSet", hasSize(2)))
                .andExpect(jsonPath("$.skillSet[0]", is("Java")))
                .andExpect(jsonPath("$.projects", hasSize(1)))
                .andExpect(jsonPath("$.projects[0].title", is("Employee Project")));
    }

    @Test
    @WithMockUser(roles = "user")
    void employeeHasNoProjects() throws Exception {
        Mockito.doReturn(
                Map.of(
                        "firstName", "Alice",
                        "lastName", "Smith",
                        "skillSet", List.of(Map.of("skill","Java"))
                )
        ).when(employeeService).getEmployeeById(10L);

        this.mockMvc.perform(get("/project/employee/{employeeId}", 10).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "user")
    void employeeDoesNotExist() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Employee not found"))
                .when(employeeService).getEmployeeById(99L);

        this.mockMvc.perform(get("/project/employee/{employeeId}", 99).with(csrf()))
                .andExpect(status().isNotFound());
    }

}
