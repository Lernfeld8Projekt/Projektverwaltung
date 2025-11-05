package de.szut.lf8_starter.project;

import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.employee.NameDTO;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetAllEmployeesFromProjectIT extends AbstractIntegrationTest {

    @MockBean
    EmployeeService employeeService;

    @Test
    void authorization() throws Exception {
        this.mockMvc.perform(get("/project/{id}/employees", 1)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void findOne() throws Exception {
        NameDTO nameDTO = new NameDTO();
        nameDTO.setLastName("Rosenbaum");
        nameDTO.setFirstName("Jarne");
        Mockito.doReturn(nameDTO).when(employeeService).getEmployeeName(1L);

        var projectEntity = new ProjectEntity();
        projectEntity.setTitle("BFK");
        projectEntity.setResponsibleEmployeeId(1L);
        projectEntity.setCustomerId(1L);
        projectEntity.setCustomerRepresentativeName("Max Meyer");
        projectEntity.setGoal("Project fertig machen");
        projectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        projectEntity.setPlannedEndDate(LocalDate.parse("2028-01-01"));

        ProjectAssignment projectAssignment = new ProjectAssignment();
        projectAssignment.setProject(projectEntity);
        projectAssignment.setEmployeeId(1L);
        projectAssignment.setQualificationId(1L);

        Set<ProjectAssignment> projectAssignments = new HashSet<>();
        projectAssignments.add(projectAssignment);

        projectEntity.setAssignments(projectAssignments);
        this.projectRepository.save(projectEntity);

        this.mockMvc.perform(get("/project/{id}/employees", projectEntity.getId())
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("id", is(projectEntity.getId().intValue())))
                .andExpect(jsonPath("title", is("BFK")))
                .andExpect(jsonPath("$.employees", hasSize(1)))
                .andExpect(jsonPath("$.employees[0].id", is(projectAssignment.getEmployeeId().intValue())))
                .andExpect(jsonPath("$.employees[0].lastName", is("Rosenbaum")))
                .andExpect(jsonPath("$.employees[0].firstName", is("Jarne")))
                .andExpect(jsonPath("$.employees[0].qualification", is(projectAssignment.getQualificationId().intValue())));
    }

    @Test
    @WithMockUser(roles = "user")
    void findAll() throws Exception {
        NameDTO firstNameDTO = new NameDTO();
        firstNameDTO.setLastName("Rosenbaum");
        firstNameDTO.setFirstName("Jarne");
        Mockito.doReturn(firstNameDTO).when(employeeService).getEmployeeName(1L);

        NameDTO secondNameDTO = new NameDTO();
        secondNameDTO.setLastName("Meier");
        secondNameDTO.setFirstName("Hans");
        Mockito.doReturn(secondNameDTO).when(employeeService).getEmployeeName(2L);

        var projectEntity = new ProjectEntity();
        projectEntity.setTitle("BFK");
        projectEntity.setResponsibleEmployeeId(1L);
        projectEntity.setCustomerId(1L);
        projectEntity.setCustomerRepresentativeName("Max Meyer");
        projectEntity.setGoal("Project fertig machen");
        projectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        projectEntity.setPlannedEndDate(LocalDate.parse("2028-01-01"));

        ProjectAssignment firstProjectAssignment = new ProjectAssignment();
        firstProjectAssignment.setProject(projectEntity);
        firstProjectAssignment.setEmployeeId(1L);
        firstProjectAssignment.setQualificationId(1L);

        ProjectAssignment secondProjectAssignment = new ProjectAssignment();
        secondProjectAssignment.setProject(projectEntity);
        secondProjectAssignment.setEmployeeId(2L);
        secondProjectAssignment.setQualificationId(1L);

        Set<ProjectAssignment> projectAssignments = new HashSet<>();
        projectAssignments.add(firstProjectAssignment);
        projectAssignments.add(secondProjectAssignment);

        projectEntity.setAssignments(projectAssignments);
        this.projectRepository.save(projectEntity);

        this.mockMvc.perform(get("/project/{id}/employees", projectEntity.getId())
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("id", is(projectEntity.getId().intValue())))
                .andExpect(jsonPath("title", is("BFK")))
                .andExpect(jsonPath("$.employees", hasSize(2)))
                .andExpect(jsonPath("$.employees[0].id", is(firstProjectAssignment.getEmployeeId().intValue())))
                .andExpect(jsonPath("$.employees[0].lastName", is("Rosenbaum")))
                .andExpect(jsonPath("$.employees[0].firstName", is("Jarne")))
                .andExpect(jsonPath("$.employees[0].qualification", is(firstProjectAssignment.getQualificationId().intValue())))

                .andExpect(jsonPath("$.employees[1].id", is(secondProjectAssignment.getEmployeeId().intValue())))
                .andExpect(jsonPath("$.employees[1].lastName", is("Meier")))
                .andExpect(jsonPath("$.employees[1].firstName", is("Hans")))
                .andExpect(jsonPath("$.employees[1].qualification", is(secondProjectAssignment.getQualificationId().intValue())));
    }

    @Test
    @WithMockUser(roles = "user")
    void getProjectWithoutEmployees() throws Exception {
        var projectEntity = new ProjectEntity();
        projectEntity.setTitle("BFK");
        projectEntity.setResponsibleEmployeeId(1L);
        projectEntity.setCustomerId(1L);
        projectEntity.setCustomerRepresentativeName("Max Meyer");
        projectEntity.setGoal("Project fertig machen");
        projectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        projectEntity.setPlannedEndDate(LocalDate.parse("2028-01-01"));

        this.projectRepository.save(projectEntity);

        this.mockMvc.perform(get("/project/{id}/employees", projectEntity.getId())
                .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("id", is(projectEntity.getId().intValue())))
                .andExpect(jsonPath("title", is("BFK")))
                .andExpect(jsonPath("$.employees", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "user")
    void getEmployeesFromNonExistingProject() throws Exception {
        this.mockMvc.perform(get("/project/{id}/employees", 1)
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Project not found on id: 1")));
    }
}
