package de.szut.lf8_starter.project;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetAllIT extends AbstractIntegrationTest {
    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void authorization() throws Exception {
        this.mockMvc.perform(get("/project")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void findAll() throws Exception {
        ProjectEntity firstProject = new ProjectEntity();
        firstProject.setTitle("1. Project");
        firstProject.setResponsibleEmployeeId(3L);
        firstProject.setCustomerId(2L);
        firstProject.setCustomerRepresentativeName("Herr Mann");
        firstProject.setGoal("Noch kein");
        firstProject.setStartDate(LocalDate.parse("2025-10-09"));
        firstProject.setPlannedEndDate(LocalDate.parse("2028-10-09"));

        ProjectEntity secondProject = new ProjectEntity();
        secondProject.setTitle("2. Project");
        secondProject.setResponsibleEmployeeId(2L);
        secondProject.setCustomerId(1L);
        secondProject.setCustomerRepresentativeName("Frau Frau");
        secondProject.setGoal("Noch kein");
        secondProject.setStartDate(LocalDate.parse("2024-10-09"));
        secondProject.setPlannedEndDate(LocalDate.parse("2028-12-09"));

        projectRepository.save(firstProject);
        projectRepository.save(secondProject);

        final var contentAsString = this.mockMvc.perform(get("/project")
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("1. Project")))
                .andExpect(jsonPath("$[0].responsibleEmployeeId", is(3)))
                .andExpect(jsonPath("$[0].customerId", is(2)))
                .andExpect(jsonPath("$[0].customerRepresentativeName", is("Herr Mann")))
                .andExpect(jsonPath("$[0].goal", is("Noch kein")))
                .andExpect(jsonPath("$[0].startDate", is("2025-10-09")))
                .andExpect(jsonPath("$[0].plannedEndDate", is("2028-10-09")))

                .andExpect(jsonPath("$[1].title", is("2. Project")))
                .andExpect(jsonPath("$[1].responsibleEmployeeId", is(2)))
                .andExpect(jsonPath("$[1].customerId", is(1)))
                .andExpect(jsonPath("$[1].customerRepresentativeName", is("Frau Frau")))
                .andExpect(jsonPath("$[1].goal", is("Noch kein")))
                .andExpect(jsonPath("$[1].startDate", is("2024-10-09")))
                .andExpect(jsonPath("$[1].plannedEndDate", is("2028-12-09")));
    }

    @Test
    @WithMockUser(roles = "user")
    void noProjectsFound() throws Exception {
        this.mockMvc.perform(get("/project")
                .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
