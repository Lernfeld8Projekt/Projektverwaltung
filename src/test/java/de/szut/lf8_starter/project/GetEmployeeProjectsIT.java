package de.szut.lf8_starter.project;

import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.employee.NameDTO;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetEmployeeProjectsIT extends AbstractIntegrationTest {
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
        NameDTO nameDTO = new NameDTO();
        nameDTO.setFirstName("John");
        nameDTO.setLastName("Doe");
        Mockito.doReturn(true).when(employeeService).checkIfEmployeeExists(1L);
        Mockito.doReturn(nameDTO).when(employeeService).getEmployeeName(1L);

        ProjectEntity project = new ProjectEntity();
        project.setTitle("Employee Project");
        project.setResponsibleEmployeeId(2L);
        project.setCustomerId(10L);
        project.setCustomerRepresentativeName("Klaus Kundin");
        project.setGoal("Test Goal");
        project.setStartDate(LocalDate.parse("2024-01-01"));
        project.setPlannedEndDate(LocalDate.parse("2024-12-31"));
        projectRepository.save(project);

        ProjectAssignment assignment = new ProjectAssignment();
        assignment.setEmployeeId(1L);
        assignment.setQualificationId(1L);
        assignment.setProject(project);
        project.getAssignments().add(assignment);
        projectRepository.save(project);

        this.mockMvc.perform(get("/project/employee/{employeeId}", 1)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(1)))
                .andExpect(jsonPath("$.employeeFirstName", is("John")))
                .andExpect(jsonPath("$.employeeLastName", is("Doe")))
                .andExpect(jsonPath("$.projects", hasSize(1)))
                .andExpect(jsonPath("$.projects[0].id", is(1)))
                .andExpect(jsonPath("$.projects[0].title", is("Employee Project")))
                .andExpect(jsonPath("$.projects[0].startDate", is("2024-01-01")))
                .andExpect(jsonPath("$.projects[0].plannedEndDate", is("2024-12-31")))
                .andExpect(jsonPath("$.projects[0].actualEndDate", nullValue()))
                .andExpect(jsonPath("$.projects[0].employeeQualification", is(1)));
    }

    @Test
    @WithMockUser(roles = "user")
    void employeeHasNoProjects() throws Exception {
        NameDTO nameDTO = new NameDTO();
        nameDTO.setFirstName("John");
        nameDTO.setLastName("Doe");
        Mockito.doReturn(nameDTO).when(employeeService).getEmployeeName(2L);
        Mockito.doReturn(true).when(employeeService).checkIfEmployeeExists(2L);

        this.mockMvc.perform(get("/project/employee/{employeeId}", 2).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(2)))
                .andExpect(jsonPath("$.employeeFirstName", is("John")))
                .andExpect(jsonPath("$.employeeLastName", is("Doe")))
                .andExpect(jsonPath("$.projects", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "user")
    void employeeDoesNotExist() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Employee not found on id: 1")).when(employeeService).checkIfEmployeeExists(1L);

        this.mockMvc.perform(get("/project/employee/{employeeId}", 1).with(csrf()))
                .andExpect(status().isNotFound());
    }
}