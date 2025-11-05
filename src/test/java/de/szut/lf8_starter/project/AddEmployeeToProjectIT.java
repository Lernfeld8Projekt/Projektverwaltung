package de.szut.lf8_starter.project;

import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.employee.NameDTO;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AddEmployeeToProjectIT extends AbstractIntegrationTest {
    @MockBean
    private EmployeeService employeeService;

    @Test
    void authorization() throws Exception {
        this.mockMvc.perform(post("/project/{projectID}", 1)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser(roles = "user")
    void addExistingEmployeeToProject() throws Exception {
        NameDTO nameDTO = new NameDTO();
        nameDTO.setLastName("Rosenbaum");
        nameDTO.setFirstName("Jarne");
        Mockito.doReturn(nameDTO).when(employeeService).getEmployeeName(2L);
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

        final String content = """
                  {
                    "employeeId": 2,
                    "qualification": 1
                }
                """;

        final var contentAsString = this.mockMvc.perform(post("/project/{projectID}", projectEntity.getId())
                        .content(content).contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("projectId", is(projectEntity.getId().intValue())))
                .andExpect(jsonPath("title", is("BFK")))
                .andExpect(jsonPath("employeeId", is(2)))
                .andExpect(jsonPath("employeeLastName", is("Rosenbaum")))
                .andExpect(jsonPath("employeeFirstName", is("Jarne")))
                .andExpect(jsonPath("qualification", is(1)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var projectId = Long.parseLong(new JSONObject(contentAsString).get("projectId").toString());

        final var project = projectRepository.findById(projectId);

        List<ProjectAssignment> projectAssignments = new ArrayList<>();
        projectAssignments.addAll(project.get().getAssignments());
        ProjectAssignment projectAssignment = projectAssignments.getFirst();

        assertThat(projectAssignment).isIn(projectAssignments);
        assertThat(projectAssignment.getId()).isNotNull();
        assertThat(projectAssignment.getProject()).isEqualTo(project.get());
        assertThat(projectAssignment.getEmployeeId()).isEqualTo(2);
        assertThat(projectAssignment.getQualificationId()).isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "user")
    void tryToAddAnAlreadyAddedEmployee() throws Exception {
        NameDTO nameDTO = new NameDTO();
        nameDTO.setLastName("Rosenbaum");
        nameDTO.setFirstName("Jarne");
        Mockito.doReturn(nameDTO).when(employeeService).getEmployeeName(1L);
        Mockito.doReturn(true).when(employeeService).checkIfEmployeeExists(1L);

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
        projectRepository.save(projectEntity);

        final String content = """
                  {
                    "employeeId": 1,
                    "qualification": 1
                }
                """;

        this.mockMvc.perform(post("/project/{projectID}", projectEntity.getId())
                .content(content).contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("The Employee with ID 1 is already a part of the project!")));
    }

    @Test
    @WithMockUser(roles = "user")
    void tryToAddANonExistentEmployee() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Employee not found on id: 2")).when(employeeService).checkIfEmployeeExists(2L);

        var projectEntity = new ProjectEntity();
        projectEntity.setTitle("BFK");
        projectEntity.setResponsibleEmployeeId(1L);
        projectEntity.setCustomerId(1L);
        projectEntity.setCustomerRepresentativeName("Max Meyer");
        projectEntity.setGoal("Project fertig machen");
        projectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        projectEntity.setPlannedEndDate(LocalDate.parse("2028-01-01"));
        projectRepository.save(projectEntity);

        final String content = """
                  {
                    "employeeId": 2,
                    "qualification": 1
                }
                """;

        this.mockMvc.perform(post("/project/{projectID}", projectEntity.getId())
                        .content(content).contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Employee not found on id: 2")));

    }

    @Test
    @WithMockUser(roles = "user")
    void tryToAddAnExistingEmployeeWithoutTheGivenQualification() throws Exception {
        NameDTO nameDTO = new NameDTO();
        nameDTO.setLastName("Rosenbaum");
        nameDTO.setFirstName("Jarne");
        Mockito.doReturn(nameDTO).when(employeeService).getEmployeeName(2L);
        Mockito.doThrow(new ResourceNotFoundException("Qualification not found for employee on ID: 3")).when(employeeService).checkIfEmployeeHaveQualification(2L, 3L);

        var projectEntity = new ProjectEntity();
        projectEntity.setTitle("BFK");
        projectEntity.setResponsibleEmployeeId(1L);
        projectEntity.setCustomerId(1L);
        projectEntity.setCustomerRepresentativeName("Max Meyer");
        projectEntity.setGoal("Project fertig machen");
        projectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        projectEntity.setPlannedEndDate(LocalDate.parse("2028-01-01"));
        projectRepository.save(projectEntity);

        final String content = """
                  {
                    "employeeId": 2,
                    "qualification": 3
                }
                """;

        this.mockMvc.perform(post("/project/{projectID}", projectEntity.getId())
                        .content(content).contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Qualification not found for employee on ID: 3")));
    }

    @Test
    @WithMockUser(roles = "user")
    void projectDoesNotExists() throws Exception {
        NameDTO nameDTO = new NameDTO();
        nameDTO.setLastName("Rosenbaum");
        nameDTO.setFirstName("Jarne");
        Mockito.doReturn(nameDTO).when(employeeService).getEmployeeName(1L);
        Mockito.doReturn(true).when(employeeService).checkIfEmployeeExists(1L);

        final String content = """
                  {
                    "employeeId": 1,
                    "qualification": 1
                }
                """;

        this.mockMvc.perform(post("/project/{projectID}", 6725812)
                        .content(content).contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Project not found on id: 6725812")));

    }

    @Test
    @WithMockUser(roles = "user")
    void employeeIsAlreadyBookedInProjectPeriod() throws Exception {
        NameDTO nameDTO = new NameDTO();
        nameDTO.setLastName("Rosenbaum");
        nameDTO.setFirstName("Jarne");
        Mockito.doReturn(nameDTO).when(employeeService).getEmployeeName(1L);
        Mockito.doReturn(true).when(employeeService).checkIfEmployeeExists(1L);

        var firstProjectEntity = new ProjectEntity();
        firstProjectEntity.setTitle("BFK");
        firstProjectEntity.setResponsibleEmployeeId(1L);
        firstProjectEntity.setCustomerId(1L);
        firstProjectEntity.setCustomerRepresentativeName("Max Meyer");
        firstProjectEntity.setGoal("Project fertig machen");
        firstProjectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        firstProjectEntity.setPlannedEndDate(LocalDate.parse("2028-01-01"));

        ProjectAssignment projectAssignment = new ProjectAssignment();
        projectAssignment.setProject(firstProjectEntity);
        projectAssignment.setEmployeeId(1L);
        projectAssignment.setQualificationId(1L);

        Set<ProjectAssignment> projectAssignments = new HashSet<>();
        projectAssignments.add(projectAssignment);

        firstProjectEntity.setAssignments(projectAssignments);
        this.projectRepository.save(firstProjectEntity);

        var secondProjectEntity = new ProjectEntity();
        secondProjectEntity.setTitle("BFK");
        secondProjectEntity.setResponsibleEmployeeId(1L);
        secondProjectEntity.setCustomerId(1L);
        secondProjectEntity.setCustomerRepresentativeName("Max Meyer");
        secondProjectEntity.setGoal("Project fertig machen");
        secondProjectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        secondProjectEntity.setPlannedEndDate(LocalDate.parse("2027-01-01"));
        this.projectRepository.save(secondProjectEntity);

        final String content = """
                  {
                    "employeeId": 1,
                    "qualification": 1
                }
                """;

        this.mockMvc.perform(post("/project/{projectID}", secondProjectEntity.getId())
                        .content(content).contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Employee is already in a project in the period from 2026-07-07 to 2028-01-01")));
    }

    @Test
    @WithMockUser(roles = "user")
    //Das Startdatum liegt vor dem geblockten Zeitraum und das Enddatum im geblockten Zeitraum
    void overlappingProjectEndDate() throws Exception {
        NameDTO nameDTO = new NameDTO();
        nameDTO.setLastName("Rosenbaum");
        nameDTO.setFirstName("Jarne");
        Mockito.doReturn(nameDTO).when(employeeService).getEmployeeName(1L);
        Mockito.doReturn(true).when(employeeService).checkIfEmployeeExists(1L);

        var firstProjectEntity = new ProjectEntity();
        firstProjectEntity.setTitle("BFK");
        firstProjectEntity.setResponsibleEmployeeId(1L);
        firstProjectEntity.setCustomerId(1L);
        firstProjectEntity.setCustomerRepresentativeName("Max Meyer");
        firstProjectEntity.setGoal("Project fertig machen");
        firstProjectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        firstProjectEntity.setPlannedEndDate(LocalDate.parse("2028-01-01"));

        ProjectAssignment projectAssignment = new ProjectAssignment();
        projectAssignment.setProject(firstProjectEntity);
        projectAssignment.setEmployeeId(1L);
        projectAssignment.setQualificationId(1L);

        Set<ProjectAssignment> projectAssignments = new HashSet<>();
        projectAssignments.add(projectAssignment);

        firstProjectEntity.setAssignments(projectAssignments);
        this.projectRepository.save(firstProjectEntity);

        var secondProjectEntity = new ProjectEntity();
        secondProjectEntity.setTitle("BFK");
        secondProjectEntity.setResponsibleEmployeeId(1L);
        secondProjectEntity.setCustomerId(1L);
        secondProjectEntity.setCustomerRepresentativeName("Max Meyer");
        secondProjectEntity.setGoal("Project fertig machen");
        secondProjectEntity.setStartDate(LocalDate.parse("2025-07-07"));
        secondProjectEntity.setPlannedEndDate(LocalDate.parse("2027-01-01"));
        this.projectRepository.save(secondProjectEntity);

        final String content = """
                  {
                    "employeeId": 1,
                    "qualification": 1
                }
                """;

        this.mockMvc.perform(post("/project/{projectID}", secondProjectEntity.getId())
                        .content(content).contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Employee is already in a project in the period from 2026-07-07 to 2028-01-01")));
    }

    @Test
    @WithMockUser(roles = "user")
        //Das Startdatum liegt in dem geblockten Zeitraum und das Enddatum hinter dem geblockten Zeitraum
    void overlappingProjectStartDate() throws Exception {
        NameDTO nameDTO = new NameDTO();
        nameDTO.setLastName("Rosenbaum");
        nameDTO.setFirstName("Jarne");
        Mockito.doReturn(nameDTO).when(employeeService).getEmployeeName(1L);
        Mockito.doReturn(true).when(employeeService).checkIfEmployeeExists(1L);

        var firstProjectEntity = new ProjectEntity();
        firstProjectEntity.setTitle("BFK");
        firstProjectEntity.setResponsibleEmployeeId(1L);
        firstProjectEntity.setCustomerId(1L);
        firstProjectEntity.setCustomerRepresentativeName("Max Meyer");
        firstProjectEntity.setGoal("Project fertig machen");
        firstProjectEntity.setStartDate(LocalDate.parse("2026-07-07"));
        firstProjectEntity.setPlannedEndDate(LocalDate.parse("2028-01-01"));

        ProjectAssignment projectAssignment = new ProjectAssignment();
        projectAssignment.setProject(firstProjectEntity);
        projectAssignment.setEmployeeId(1L);
        projectAssignment.setQualificationId(1L);

        Set<ProjectAssignment> projectAssignments = new HashSet<>();
        projectAssignments.add(projectAssignment);

        firstProjectEntity.setAssignments(projectAssignments);
        this.projectRepository.save(firstProjectEntity);

        var secondProjectEntity = new ProjectEntity();
        secondProjectEntity.setTitle("BFK");
        secondProjectEntity.setResponsibleEmployeeId(1L);
        secondProjectEntity.setCustomerId(1L);
        secondProjectEntity.setCustomerRepresentativeName("Max Meyer");
        secondProjectEntity.setGoal("Project fertig machen");
        secondProjectEntity.setStartDate(LocalDate.parse("2026-09-07"));
        secondProjectEntity.setPlannedEndDate(LocalDate.parse("2029-01-01"));
        this.projectRepository.save(secondProjectEntity);

        final String content = """
                  {
                    "employeeId": 1,
                    "qualification": 1
                }
                """;

        this.mockMvc.perform(post("/project/{projectID}", secondProjectEntity.getId())
                        .content(content).contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Employee is already in a project in the period from 2026-07-07 to 2028-01-01")));
    }
}
