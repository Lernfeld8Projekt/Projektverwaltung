package de.szut.lf8_starter.project;

import de.szut.lf8_starter.examples.hello.dto.HelloCreateDto;
import de.szut.lf8_starter.examples.hello.dto.HelloGetDto;
import de.szut.lf8_starter.project.DTO.AddProjectDTO;
import de.szut.lf8_starter.project.DTO.GetProjectDTO;
import de.szut.lf8_starter.project.DTO.PatchProjectDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ProjectControllerOpenAPI {
    @Operation(summary = "creates a new project with it's id, title," +
            " responsible employee id, customer id, customer representative name," +
            " goal, start date, planned end date and actual end date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created project",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetProjectDTO.class))}),
            @ApiResponse(responseCode = "400", description = "invalid JSON posted",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content)
    })
    ResponseEntity<GetProjectDTO> createProject(AddProjectDTO addProjectDTO);

    @Operation(summary = "patches an existing project by it's id with it's title," +
            " responsible employee id, customer id, customer representative name," +
            " goal, start date, planned end date and actual end date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "patched project",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetProjectDTO.class))}),
            @ApiResponse(responseCode = "400", description = "invalid JSON posted",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Project, Employee or Customer not found",
                    content = @Content),
    })
    ResponseEntity<GetProjectDTO> updateProject(Long id, PatchProjectDTO patchProjectDTO);
}
