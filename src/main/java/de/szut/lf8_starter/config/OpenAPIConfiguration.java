package de.szut.lf8_starter.config;



import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.ServletContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenAPIConfiguration {

    private ServletContext context;

    public OpenAPIConfiguration(ServletContext context) {
        this.context = context;
    }


    @Bean
    public OpenAPI springShopOpenAPI(
    ) {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addServersItem(new Server().url(this.context.getContextPath()))
                .info(new Info()
                        .title("Project Management Service")
                        .description("The Project Management Service API manages the projects of HighTec GmbH, including their assigned employees and " +
                                "related information such as customers and project goals." +
                                "It offers functionality to create, read, update, and delete projects, as well as to assign, remove or read employees to existing projects. " +
                                "The API is organized around REST. It has predictable resource-oriented URLs, accepts JSON-encoded request bodies, " +
                                "returns JSON-encoded responses, uses standard HTTP response codes and authentication.")

                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }


}