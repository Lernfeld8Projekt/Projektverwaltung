package de.szut.lf8_starter.mapper;

import de.szut.lf8_starter.employee.EmployeeService;
import de.szut.lf8_starter.employee.NameDTO;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.DTO.*;
import de.szut.lf8_starter.project.ProjectAssignment;
import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class MappingService {
    private final ProjectRepository projectRepository;
    private final EmployeeService employeeService;

    public MappingService(ProjectRepository projectRepository, EmployeeService employeeService) {
        this.projectRepository = projectRepository;
        this.employeeService = employeeService;
    }

    public ProjectEntity mapAddProjectDTOtoProjectEntity(AddProjectDTO addProjectDTO) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setTitle(addProjectDTO.getTitle());
        projectEntity.setResponsibleEmployeeId(addProjectDTO.getResponsibleEmployeeId());
        projectEntity.setCustomerId(addProjectDTO.getCustomerId());
        projectEntity.setCustomerRepresentativeName(addProjectDTO.getCustomerRepresentativeName());
        projectEntity.setGoal(addProjectDTO.getGoal());
        projectEntity.setStartDate(addProjectDTO.getStartDate());
        projectEntity.setPlannedEndDate(addProjectDTO.getPlannedEndDate());
        return projectEntity;
    }

    public GetProjectDTO mapProjectEntityToGetProjectDTO(ProjectEntity projectEntity) {
        GetProjectDTO getProjectDTO = new GetProjectDTO();
        getProjectDTO.setId(projectEntity.getId());
        getProjectDTO.setTitle(projectEntity.getTitle());
        getProjectDTO.setResponsibleEmployeeId(projectEntity.getResponsibleEmployeeId());
        getProjectDTO.setCustomerId(projectEntity.getCustomerId());
        getProjectDTO.setCustomerRepresentativeName(projectEntity.getCustomerRepresentativeName());
        getProjectDTO.setGoal(projectEntity.getGoal());
        getProjectDTO.setStartDate(projectEntity.getStartDate());
        getProjectDTO.setPlannedEndDate(projectEntity.getPlannedEndDate());
        getProjectDTO.setActualEndDate(projectEntity.getActualEndDate());
        return getProjectDTO;
    }

    public Map<String, Object> mapPatchProjectDTOtoMapWithFields(PatchProjectDTO patchProjectDTO) {
        Map<String, Object> fields = new HashMap<>();

        if (patchProjectDTO == null) {
            return fields;
        }

        for (Field field : patchProjectDTO.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(patchProjectDTO);
                if (value != null) {
                    fields.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Could not access fields!");
            }
        }

        return fields;
    }

    public List<GetProjectDTO> mapProjectListToGetProjectDTOList(List<ProjectEntity> projects) {
        List<GetProjectDTO> getProjectDTOList = new ArrayList<>();
        for (ProjectEntity projectEntity : projects) {
            getProjectDTOList.add(this.mapProjectEntityToGetProjectDTO(projectEntity));
        }
        return getProjectDTOList;
    }

    public ProjectAssignment mapAddEmployeeToProjectDTOToProjectAssignment(Long projectId, AddEmployeeToProjectDTO addEmployeeToProjectDTO) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found on id: " + projectId));

        ProjectAssignment projectAssignment = new ProjectAssignment();
        projectAssignment.setProject(projectEntity);
        projectAssignment.setEmployeeId(addEmployeeToProjectDTO.getEmployeeId());
        projectAssignment.setQualificationId(addEmployeeToProjectDTO.getQualification());
        return projectAssignment;
    }

    public GetProjectEmployeeDTO mapProjectAssignmentToGetProjectEmployeeDTO(ProjectAssignment projectAssignment, NameDTO nameDTO) {
        GetProjectEmployeeDTO getProjectEmployeeDTO = new GetProjectEmployeeDTO();
        getProjectEmployeeDTO.setProjectId(projectAssignment.getProject().getId());
        getProjectEmployeeDTO.setTitle(projectAssignment.getProject().getTitle());
        getProjectEmployeeDTO.setEmployeeId(projectAssignment.getEmployeeId());
        getProjectEmployeeDTO.setEmployeeLastName(nameDTO.getLastName());
        getProjectEmployeeDTO.setEmployeeFirstName(nameDTO.getFirstName());
        getProjectEmployeeDTO.setQualification(projectAssignment.getQualificationId());
        return getProjectEmployeeDTO;
    }

    public GetEmployeeProjectsDTO mapEmployeeProjects(Long employeeId, NameDTO name, List<ProjectEntity> projects) {
        GetEmployeeProjectsDTO response = new GetEmployeeProjectsDTO();
        response.setEmployeeId(employeeId);
        response.setEmployeeFirstName(name.getFirstName());
        response.setEmployeeLastName(name.getLastName());
        List<GetProjectFromEmployeeDTO> getProjectFromEmployeeDTOList = new ArrayList<>();
        for (ProjectEntity project : projects) {
            GetProjectFromEmployeeDTO getProjectFromEmployeeDTO = mapProjectToGetProjectFromEmployeeDTO(employeeId, project);
            getProjectFromEmployeeDTOList.add(getProjectFromEmployeeDTO);
        }
        response.setProjects(getProjectFromEmployeeDTOList);
        return response;
    }

    private static GetProjectFromEmployeeDTO mapProjectToGetProjectFromEmployeeDTO(Long employeeId, ProjectEntity project) {
        GetProjectFromEmployeeDTO getProjectFromEmployeeDTO = new GetProjectFromEmployeeDTO();
        getProjectFromEmployeeDTO.setId(project.getId());
        getProjectFromEmployeeDTO.setTitle(project.getTitle());
        getProjectFromEmployeeDTO.setStartDate(project.getStartDate());
        getProjectFromEmployeeDTO.setPlannedEndDate(project.getPlannedEndDate());
        getProjectFromEmployeeDTO.setActualEndDate(project.getActualEndDate());
        Set<ProjectAssignment> projectAssignmentsSet = project.getAssignments();
        for (ProjectAssignment assignment : projectAssignmentsSet) {
            if (assignment.getEmployeeId().equals(employeeId)) {
                getProjectFromEmployeeDTO.setEmployeeQualification(assignment.getQualificationId());
            }
        }
        return getProjectFromEmployeeDTO;
    }

    public GetAllEmployeesFromProjectDTO mapProjectEntityToGetAllEmployeesFromProjectDTO(ProjectEntity project) {
        GetAllEmployeesFromProjectDTO projectDTO = new GetAllEmployeesFromProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setTitle(project.getTitle());

        List<GetEmployeeDTO> employees = new ArrayList<>();
        for (ProjectAssignment projectAssignment : project.getAssignments()) {
            NameDTO nameDTO = this.employeeService.getEmployeeName(projectAssignment.getEmployeeId());
            GetEmployeeDTO getEmployeeDTO = new GetEmployeeDTO();
            getEmployeeDTO.setId(projectAssignment.getEmployeeId());
            getEmployeeDTO.setLastName(nameDTO.getLastName());
            getEmployeeDTO.setFirstName(nameDTO.getFirstName());
            getEmployeeDTO.setQualification(projectAssignment.getQualificationId());
            employees.add(getEmployeeDTO);
        }

        projectDTO.setEmployees(employees);
        return projectDTO;
    }
}