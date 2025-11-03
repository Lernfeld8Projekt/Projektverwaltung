package de.szut.lf8_starter.project;

import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.employee.NameDTO;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
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
    @WithMockUser(roles = "user")
    void addExistingEmployeeToProject() throws Exception {
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
        projectRepository.save(projectEntity);

        final String content = """
                  {
                    "employeeId": 1,
                    "qualification": 1,
                }
                """;

        final var contentAsString = this.mockMvc.perform(post("/project/{projectID}", projectEntity.getId())
                        .content(content).contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("projectId", is(1)))
                .andExpect(jsonPath("title", is("BFK")))
                .andExpect(jsonPath("employeeId", is(1)))
                .andExpect(jsonPath("employeeLastName", is("Rosenbaum")))
                .andExpect(jsonPath("employeeFirstName", is("Jarne")))
                .andExpect(jsonPath("qualification", is(1)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var projectId = Long.parseLong(new JSONObject(contentAsString).get("projectId").toString());

        final var project = projectRepository.findById(projectId);

        Set<ProjectAssignment> projectAssignments = project.get().getAssignments();

    }
}
