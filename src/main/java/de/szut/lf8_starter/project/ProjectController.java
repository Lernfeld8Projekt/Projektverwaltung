package de.szut.lf8_starter.project;

import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.employee.NameDTO;
import de.szut.lf8_starter.mapper.MappingService;
import de.szut.lf8_starter.project.DTO.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/project")
public class ProjectController implements ProjectControllerOpenAPI {
    private final MappingService mappingService;
    private final ProjectService projectService;
    private final EmployeeService employeeService;

    public ProjectController(MappingService mappingService, ProjectService projectService, EmployeeService employeeService) {
        this.mappingService = mappingService;
        this.projectService = projectService;
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<GetProjectDTO> createProject(@Valid @RequestBody final AddProjectDTO addProjectDTO) {
        ProjectEntity projectEntity = this.mappingService.mapAddProjectDTOtoProjectEntity(addProjectDTO);
        projectEntity = this.projectService.createProject(projectEntity);
        GetProjectDTO projectDTO = this.mappingService.mapProjectEntityToGetProjectDTO(projectEntity);
        return new ResponseEntity<>(projectDTO, CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GetProjectDTO>> getAllProjects() {
        List<ProjectEntity> projects = this.projectService.getAllProjects();
        List<GetProjectDTO> getProjectDTOList = this.mappingService.mapProjectListToGetProjectDTOList(projects);
        return new ResponseEntity<>(getProjectDTOList, OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<GetProjectDTO> getProjectById(@PathVariable Long id) {
        ProjectEntity project = this.projectService.getProjectById(id);
        GetProjectDTO getProjectDTO = this.mappingService.mapProjectEntityToGetProjectDTO(project);
        return new ResponseEntity<>(getProjectDTO, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id){
        projectService.deleteProjectById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GetProjectDTO> updateProject(@PathVariable Long id, @RequestBody PatchProjectDTO patchProjectDTO) {
        Map<String,Object> fields = this.mappingService.mapPatchProjectDTOtoMapWithFields(patchProjectDTO);
        ProjectEntity projectEntity = this.projectService.patchProject(id, fields);
        GetProjectDTO projectDTO = this.mappingService.mapProjectEntityToGetProjectDTO(projectEntity);
        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }

    @PostMapping("/{projectID}")
    public ResponseEntity<GetProjectEmployeeDTO> addEmployeeToProject(@PathVariable Long projectID, @Valid @RequestBody AddEmployeeToProjectDTO addEmployeeToProjectDTO) {
        ProjectAssignment projectAssignment = this.mappingService.mapAddEmployeeToProjectDTOToProjectAssignment(projectID, addEmployeeToProjectDTO);
        this.projectService.addEmployeeToProject(projectID, projectAssignment);
        NameDTO nameDTO = this.employeeService.getEmployeeName(projectAssignment.getEmployeeId());
        GetProjectEmployeeDTO getProjectEmployeeDTO = this.mappingService.mapProjectAssignmentToGetProjectEmployeeDTO(projectAssignment, nameDTO);
        return new ResponseEntity<>(getProjectEmployeeDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/{employeeId}")
    public ResponseEntity<Void> removeEmployeeFromProject(@PathVariable Long id, @PathVariable Long employeeId){
        projectService.removeEmployeeFromProject(id, employeeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
