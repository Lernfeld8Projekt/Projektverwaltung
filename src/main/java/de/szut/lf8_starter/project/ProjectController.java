package de.szut.lf8_starter.project;

import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.project.DTO.GetEmployeeProjectsDTO;
import de.szut.lf8_starter.mapper.MappingService;
import de.szut.lf8_starter.project.DTO.AddProjectDTO;
import de.szut.lf8_starter.project.DTO.GetProjectDTO;
import de.szut.lf8_starter.project.DTO.PatchProjectDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
public class ProjectController implements ProjectControllerOpenAPI {
    private final MappingService mappingService;
    private final ProjectService projectService;

    public ProjectController(MappingService mappingService, ProjectService projectService) {
        this.mappingService = mappingService;
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<GetProjectDTO> createProject(@Valid @RequestBody AddProjectDTO addProjectDTO) {
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

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<GetEmployeeProjectsDTO> getProjectsByEmployeeId(@PathVariable Long employeeId) {

        Map<String, Object> employeeData = projectService.getEmployeeService().getEmployeeById(employeeId);

        List<ProjectEntity> projects = projectService.getProjectsByEmployeeId(employeeId);
        List<GetProjectDTO> projectDTOs = mappingService.mapProjectListToGetProjectDTOList(projects);

        GetEmployeeProjectsDTO response = mappingService.mapEmployeeProjects(employeeId, employeeData, projectDTOs);

        return ResponseEntity.ok(response);
    }
}
