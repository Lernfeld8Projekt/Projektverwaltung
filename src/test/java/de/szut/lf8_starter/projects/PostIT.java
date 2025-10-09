package de.szut.lf8_starter.projects;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostIT extends AbstractIntegrationTest {

    @Test
    void authorization() throws Exception {
        final String content = """
                {
                    "title": "BFK",
                    "responsibleEmployeeId": 1,
                    "customerId": 1,
                    "customerRepresentativeName": "Max Meyer",
                    "goal": "Project fertig machen",
                    "startDate": "2026-07-07",
                    "plannedEndDate": "2028-01-01"
                }
                """;

        final var contentAsString = this.mockMvc.perform(post("/projects")
                        .content(content).contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "user")
    void storeAndFind() throws Exception {

        //Body erstellen als String
        final String content = """
                  {
                    "title": "BFK",
                    "responsibleEmployeeId": 1,
                    "customerId": 1,
                    "customerRepresentativeName": "Max Meyer",
                    "goal": "Project fertig machen",
                    "startDate": "2026-07-07",
                    "plannedEndDate": "2028-01-01"
                }
                """;

        //Body posten und überprüfen ob der JSON richtig formatiert ist
        final var contentAsString = this.mockMvc.perform(post("/projects").content(content).contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title", is("BFK")))
                .andExpect(jsonPath("responsibleEmployeeId", is(1)))
                .andExpect(jsonPath("customerId", is(1)))
                .andExpect(jsonPath("customerRepresentativeName", is("Max Meyer")))
                .andExpect(jsonPath("goal", is("Project fertig machen")))
                .andExpect(jsonPath("startDate", is("2026-07-07")))
                .andExpect(jsonPath("plannedEndDate", is("2028-01-01")))
                .andReturn()
                .getResponse()
                .getContentAsString();


        //ID aus String rausziehen
        final var id = Long.parseLong(new JSONObject(contentAsString).get("id").toString());

        //Entity mit ID laden
        final var loadedEntity = projectRepository.findById(id);

        //Überprüfen ob Entity richtig in der Datenbank gespeichert wurde
        assertThat(loadedEntity).isPresent();
        assertThat(loadedEntity.get().getId()).isEqualTo(id);
        assertThat(loadedEntity.get().getTitle()).isEqualTo("BFK");
        assertThat(loadedEntity.get().getResponsibleEmployeeId()).isEqualTo(1);
        assertThat(loadedEntity.get().getCustomerId()).isEqualTo(1);
        assertThat(loadedEntity.get().getCustomerRepresentativeName()).isEqualTo("Max Meyer");
        assertThat(loadedEntity.get().getGoal()).isEqualTo("Project fertig machen");
        assertThat(loadedEntity.get().getStartDate()).isEqualTo("2026-07-07");
        assertThat(loadedEntity.get().getPlannedEndDate()).isEqualTo("2028-01-01");
    }
}
