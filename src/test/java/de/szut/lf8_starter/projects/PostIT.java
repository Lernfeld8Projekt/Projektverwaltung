package de.szut.lf8_starter.projects;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
