package de.szut.lf8_starter.project;

import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectEntity createProject(ProjectEntity projectEntity){
        return this.projectRepository.save(projectEntity);
    }
}
