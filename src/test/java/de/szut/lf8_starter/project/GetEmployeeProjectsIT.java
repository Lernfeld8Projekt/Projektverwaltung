package de.szut.lf8_starter.project;

import de.szut.lf8_starter.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetEmployeeProjectsIT extends AbstractIntegrationTest {
    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void authorization() throws Exception {
        this.mockMvc.perform(get("/project/employee/{employeeId}")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
