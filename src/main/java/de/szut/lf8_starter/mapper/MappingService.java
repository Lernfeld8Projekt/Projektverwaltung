package de.szut.lf8_starter.mapper;

import de.szut.lf8_starter.project.DTO.AddProjectDTO;
import de.szut.lf8_starter.project.DTO.GetProjectDTO;
import de.szut.lf8_starter.project.DTO.PatchProjectDTO;
import de.szut.lf8_starter.project.ProjectEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Service
public class MappingService {
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
        Map<String,Object> fields = new HashMap<>();

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
    public List<GetProjectDTO> mapProjectListToGetProjectDTOList(List<ProjectEntity> projects) {
        List<GetProjectDTO> getProjectDTOList = new ArrayList<>();
        for (ProjectEntity projectEntity : projects) {
            getProjectDTOList.add(this.mapProjectEntityToGetProjectDTO(projectEntity));
        }
        return getProjectDTOList;
    }
}