package de.szut.lf8_starter.project;

import de.szut.lf8_starter.project.DTO.AddProjectDTO;
import de.szut.lf8_starter.project.DTO.GetEmployeeProjectsDTO;
import de.szut.lf8_starter.project.DTO.GetProjectDTO;
import de.szut.lf8_starter.project.DTO.PatchProjectDTO;
import de.szut.lf8_starter.project.DTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProjectControllerOpenAPI {
    @Operation(summary = "Creates a new project with its id, title," +
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

    @Operation(summary = "Patches an existing project by its id with its title," +
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

    @Operation(summary = "Delete a project with its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project Deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid JSON posted",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "ID not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Invalid JSON posted",
                    content = @Content)})
    ResponseEntity<Void> deleteProject(Long id);

    @Operation(summary = "delivers a list of project objects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of projects",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetProjectDTO.class))}),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content)})
    ResponseEntity<List<GetProjectDTO>> getAllProjects();

    @Operation(summary = "Delivers a project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "project with given id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetProjectDTO.class))}),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "resource not found",
                    content = @Content)})
    ResponseEntity<GetProjectDTO> getProjectById(Long id);

    @Operation(summary = "Add an Employee to an existing project with project id, " +
            " the employee id and the qualification id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "added employee to project",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetProjectEmployeeDTO.class))}),
            @ApiResponse(responseCode = "400", description = "invalid JSON posted",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "conflict while adding a employee to a project.",
                    content = @Content),
    })
    ResponseEntity<GetProjectEmployeeDTO> addEmployeeToProject(Long projectID, AddEmployeeToProjectDTO addEmployeeToProjectDTO);

    @Operation(summary = "Remove an Employee from an existing project with project id and the employee id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "removes employee from project",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "invalid JSON posted",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "employee is not in the project.",
                    content = @Content)
    })
    ResponseEntity<Void> removeEmployeeFromProject(Long projectID, Long employeeID);

    @Operation(summary = "Show all Projects of employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "projects with given employee-id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetEmployeeProjectsDTO.class))}),
            @ApiResponse(responseCode = "401", description = "not authorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "resource not found",
                    content = @Content)})
    ResponseEntity<GetEmployeeProjectsDTO> getProjectsByEmployeeId(Long id);
}