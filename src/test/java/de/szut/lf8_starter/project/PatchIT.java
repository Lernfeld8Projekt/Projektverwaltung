package de.szut.lf8_starter.project;

import de.szut.lf8_starter.customer.CustomerService;
import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PatchIT extends AbstractIntegrationTest {
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private CustomerService customerService;

    @Test
    void authorization() throws Exception {
        final String content = """
                {
                    "title": "BFK",
                    "responsibleEmployeeId": 1,
                }
                """;

        final var contentAsString = this.mockMvc.perform(patch("/project/{id}", 1)
                        .content(content).contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void projectDoesNotExists() throws Exception {
        final String content = """
                  {
                    "title": "Miau",
                    "responsibleEmployeeId": 2
                }
                """;

        this.mockMvc.perform(
                        patch("/project/{id}", 6725812)
                                .content(content).contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Project not found on id: 6725812")));
    }

    @Test
    @WithMockUser(roles = "user")
    void employeeDoesNotExists() throws Exception {
        Mockito.doReturn(true).when(customerService).checkIfCustomerExists(2L);
        Mockito.doReturn(false).when(employeeService).checkIfEmployeeExists(2L);
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
                    "title": "Miau",
                    "responsibleEmployeeId": 2,
                    "customerId": 2,
                    "customerRepresentativeName": "Lukas Müller",
                    "goal": "Hallo sagen können!",
                    "startDate": "2025-07-07",
                    "plannedEndDate": "2026-01-01"
                }
                """;

        this.mockMvc.perform(
                        patch("/project/{id}", 1)
                                .content(content).contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Employee not found on id: 2")));
    }

    @Test
    @WithMockUser(roles = "user")
    void customerDoesNotExists() throws Exception {
        Mockito.doReturn(false).when(customerService).checkIfCustomerExists(2L);
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

        final String content = """
                  {
                    "title": "Miau",
                    "responsibleEmployeeId": 2,
                    "customerId": 2,
                    "customerRepresentativeName": "Lukas Müller",
                    "goal": "Hallo sagen können!",
                    "startDate": "2025-07-07",
                    "plannedEndDate": "2026-01-01"
                }
                """;

        this.mockMvc.perform(
                        patch("/project/{id}", 1)
                                .content(content).contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Customer not found on id: 2")));
    }

    @Test
    @WithMockUser(roles = "user")
    void updateWholeProjectAndFind() throws Exception {
        Mockito.doReturn(true).when(customerService).checkIfCustomerExists(2L);
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

        final String content = """
                  {
                    "title": "Miau",
                    "responsibleEmployeeId": 2,
                    "customerId": 2,
                    "customerRepresentativeName": "Lukas Müller",
                    "goal": "Hallo sagen können!",
                    "startDate": "2025-07-07",
                    "plannedEndDate": "2026-01-01"
                }
                """;

        final var contentAsString = this.mockMvc.perform(
                        patch("/project/{id}", 1)
                                .content(content).contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title", is("Miau")))
                .andExpect(jsonPath("responsibleEmployeeId", is(2)))
                .andExpect(jsonPath("customerId", is(2)))
                .andExpect(jsonPath("customerRepresentativeName", is("Lukas Müller")))
                .andExpect(jsonPath("goal", is("Hallo sagen können!")))
                .andExpect(jsonPath("startDate", is("2025-07-07")))
                .andExpect(jsonPath("plannedEndDate", is("2026-01-01")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var id = Long.parseLong(new JSONObject(contentAsString).get("id").toString());

        final var loadedEntity = projectRepository.findById(id);

        assertThat(loadedEntity).isPresent();
        assertThat(loadedEntity.get().getId()).isEqualTo(id);
        assertThat(loadedEntity.get().getTitle()).isEqualTo("Miau");
        assertThat(loadedEntity.get().getResponsibleEmployeeId()).isEqualTo(2);
        assertThat(loadedEntity.get().getCustomerId()).isEqualTo(2);
        assertThat(loadedEntity.get().getCustomerRepresentativeName()).isEqualTo("Lukas Müller");
        assertThat(loadedEntity.get().getGoal()).isEqualTo("Hallo sagen können!");
        assertThat(loadedEntity.get().getStartDate()).isEqualTo("2025-07-07");
        assertThat(loadedEntity.get().getPlannedEndDate()).isEqualTo("2026-01-01");
    }

    @Test
    @WithMockUser(roles = "user")
    void patchProjectAndFind() throws Exception {
        Mockito.doReturn(true).when(customerService).checkIfCustomerExists(2L);
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

        final String content = """
                  {
                    "title": "Miau",
                    "responsibleEmployeeId": 2,
                    "customerId": 2
                }
                """;

        final var contentAsString = this.mockMvc.perform(
                        patch("/project/{id}", 1)
                                .content(content).contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title", is("Miau")))
                .andExpect(jsonPath("responsibleEmployeeId", is(2)))
                .andExpect(jsonPath("customerId", is(2)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var id = Long.parseLong(new JSONObject(contentAsString).get("id").toString());

        final var loadedEntity = projectRepository.findById(id);

        assertThat(loadedEntity).isPresent();
        assertThat(loadedEntity.get().getId()).isEqualTo(id);
        assertThat(loadedEntity.get().getTitle()).isEqualTo("Miau");
        assertThat(loadedEntity.get().getResponsibleEmployeeId()).isEqualTo(2);
        assertThat(loadedEntity.get().getCustomerId()).isEqualTo(2);
        assertThat(loadedEntity.get().getCustomerRepresentativeName()).isEqualTo("Max Meyer");
        assertThat(loadedEntity.get().getGoal()).isEqualTo("Project fertig machen");
        assertThat(loadedEntity.get().getStartDate()).isEqualTo("2026-07-07");
        assertThat(loadedEntity.get().getPlannedEndDate()).isEqualTo("2028-01-01");
    }

    @Test
    @WithMockUser(roles = "user")
    void whenStartDateIsAfterPlannedEndDate() throws Exception {
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
                    "startDate": "2027-01-01",
                    "plannedEndDate": "2026-01-01"
                }
                """;

        this.mockMvc.perform(
                        patch("/project/{id}", 1)
                                .content(content).contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Start date cannot be after planned end date!")));
    }

    @Test
    @WithMockUser(roles = "user")
    void whenActualEndDateIsBeforeStartDate() throws Exception {
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
                    "actualEndDate": "2024-01-01"
                }
                """;

        this.mockMvc.perform(
                        patch("/project/{id}", 1)
                                .content(content).contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Actual end date cannot be before start date!")));
    }
}