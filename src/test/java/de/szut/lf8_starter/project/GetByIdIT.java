package de.szut.lf8_starter.project;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetByIdIT extends AbstractIntegrationTest {
    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void authorization() throws Exception {
        this.mockMvc.perform(get("/project/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void findById() throws Exception {
        ProjectEntity project = new ProjectEntity();
        project.setTitle("Good Project");
        project.setResponsibleEmployeeId(3L);
        project.setCustomerId(2L);
        project.setCustomerRepresentativeName("Herr Mann");
        project.setGoal("Noch kein");
        project.setStartDate(LocalDate.parse("2025-10-09"));
        project.setPlannedEndDate(LocalDate.parse("2028-10-09"));

        projectRepository.save(project);

        final var contentAsString = this.mockMvc.perform(get("/project/1")
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("title", is("Good Project")))
                .andExpect(jsonPath("responsibleEmployeeId", is(3)))
                .andExpect(jsonPath("customerId", is(2)))
                .andExpect(jsonPath("customerRepresentativeName", is("Herr Mann")))
                .andExpect(jsonPath("goal", is("Noch kein")))
                .andExpect(jsonPath("startDate", is("2025-10-09")))
                .andExpect(jsonPath("plannedEndDate", is("2028-10-09")));
    }

    @Test
    @WithMockUser(roles = "user")
    void noProjectByIdFound() throws Exception {
        this.projectRepository.deleteAll();
        this.mockMvc.perform(get("/project/1")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Project not found on id: 1")));
    }
}