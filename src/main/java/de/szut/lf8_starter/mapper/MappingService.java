package de.szut.lf8_starter.mapper;

import de.szut.lf8_starter.employee.NameDTO;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.project.DTO.*;
import de.szut.lf8_starter.project.ProjectAssignment;
import de.szut.lf8_starter.project.DTO.AddProjectDTO;
import de.szut.lf8_starter.project.DTO.GetEmployeeProjectsDTO;
import de.szut.lf8_starter.project.DTO.GetProjectDTO;
import de.szut.lf8_starter.project.DTO.PatchProjectDTO;
import de.szut.lf8_starter.project.ProjectEntity;
import de.szut.lf8_starter.project.ProjectRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Service
public class MappingService {
    private final ProjectRepository projectRepository;

    public MappingService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectEntity mapAddProjectDTOtoProjectEntity(AddProjectDTO addProjectDTO){
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

    public GetProjectDTO mapProjectEntityToGetProjectDTO(ProjectEntity projectEntity){
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

    public Map<String,Object> mapPatchProjectDTOtoMapWithFields(PatchProjectDTO patchProjectDTO) {
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
                .orElseThrow(() -> new ResourceNotFoundException("Project not found on ID: " + projectId));

        ProjectAssignment projectAssignment = new ProjectAssignment();
        projectAssignment.setProject(projectEntity);
        projectAssignment.setEmployeeId(addEmployeeToProjectDTO.getEmployeeId());
        projectAssignment.setQualificationId(addEmployeeToProjectDTO.getQualification());
        return projectAssignment;
    }

    public GetProjectEmployeeDTO mapProjectAssignmentToGetProjectEmployeeDTO(ProjectAssignment projectAssignment, NameDTO name) {
        GetProjectEmployeeDTO getProjectEmployeeDTO = new GetProjectEmployeeDTO();
        getProjectEmployeeDTO.setProjectId(projectAssignment.getProject().getId());
        getProjectEmployeeDTO.setTitle(projectAssignment.getProject().getTitle());
        getProjectEmployeeDTO.setEmployeeId(projectAssignment.getEmployeeId());
        getProjectEmployeeDTO.setEmployeeLastName(name.getLastName());
        getProjectEmployeeDTO.setEmployeeFirstName(name.getFirstName());
        getProjectEmployeeDTO.setQualification(projectAssignment.getQualificationId());
        return getProjectEmployeeDTO;
    }

    public GetEmployeeProjectsDTO mapEmployeeProjects(Long employeeId, Map<String, Object> employeeData, List<GetProjectDTO> projectDTOs) {
        String firstName = (String) employeeData.get("firstName");
        String lastName = (String) employeeData.get("lastName");

        List<Map<String, Object>> skillMaps =
                (List<Map<String, Object>>) employeeData.get("skillSet");

        List<String> skills = skillMaps.stream()
                .map(skill -> (String) skill.get("skill"))
                .toList();

        GetEmployeeProjectsDTO response = new GetEmployeeProjectsDTO();
        response.setEmployeeId(employeeId);
        response.setEmployeeName(firstName + " " + lastName);
        response.setSkillSet(skills);
        response.setProjects(projectDTOs);
        return response;
    }
}