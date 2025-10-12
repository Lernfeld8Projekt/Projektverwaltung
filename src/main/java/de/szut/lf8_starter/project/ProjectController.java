package de.szut.lf8_starter.project;

import de.szut.lf8_starter.mapper.MappingService;
import de.szut.lf8_starter.project.DTO.AddProjectDTO;
import de.szut.lf8_starter.project.DTO.GetProjectDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
public class ProjectController implements ProjectControllerOpenAPI{

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
        return new ResponseEntity<>(projectDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectById(@PathVariable final Long id){
        projectService.deleteProjectById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
