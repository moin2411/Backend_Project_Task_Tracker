package com.example.demo.service;

import com.example.demo.model.Project;
import com.example.demo.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    //Create a new project
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    //Get all projects
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    //Get a project by ID
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    //Update an existing project
    public Project updateProject(Long id, Project projectDetails) {
        // Retrieve the project to be updated from the database
        Project project = getProjectById(id); // Uses the method that checks if project exists

        // Update the project fields with the details from the request
        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        project.setStartDate(projectDetails.getStartDate());
        project.setEndDate(projectDetails.getEndDate());
        project.setStatus(projectDetails.getStatus());

        //Save and return the updated project
        return projectRepository.save(project);
    }
    //Delete a project
    public void deleteProject(Long id) {
        // Check if project exists before deleting
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }
}
