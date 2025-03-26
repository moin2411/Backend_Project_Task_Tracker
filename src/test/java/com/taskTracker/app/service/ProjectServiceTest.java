package com.taskTracker.app.service;

import com.taskTracker.app.model.Project;
import com.taskTracker.app.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project1;
    private Project project2;

    @BeforeEach
    void setUp() {
        project1 = new Project(1L, "Project Alpha", "Description Alpha",
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 6, 1), "IN_PROGRESS", new ArrayList<>());

        project2 = new Project(2L, "Project Beta", "Description Beta",
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 7, 1), "NOT_STARTED", new ArrayList<>());
    }


    @Test
    void testCreateProject() {
        when(projectRepository.save(any(Project.class))).thenReturn(project1);
        Project createdProject = projectService.createProject(project1);
        assertNotNull(createdProject);
        assertEquals("Project Alpha", createdProject.getName());
        verify(projectRepository, times(1)).save(project1);
    }

    @Test
    void testGetAllProjects() {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(project1, project2));
        List<Project> projects = projectService.getAllProjects();
        assertEquals(2, projects.size());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testGetProjectById_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        Project foundProject = projectService.getProjectById(1L);
        assertNotNull(foundProject);
        assertEquals("Project Alpha", foundProject.getName());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProjectById_NotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> projectService.getProjectById(99L));
        assertEquals("Project not found with id: 99", exception.getMessage());
        verify(projectRepository, times(1)).findById(99L);
    }

    @Test
    void testUpdateProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        when(projectRepository.save(any(Project.class))).thenReturn(project1);

        project1.setName("Updated Project Alpha");
        Project updatedProject = projectService.updateProject(1L, project1);

        assertNotNull(updatedProject);
        assertEquals("Updated Project Alpha", updatedProject.getName());
        verify(projectRepository, times(1)).save(project1);
    }

    @Test
    void testDeleteProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        doNothing().when(projectRepository).delete(project1);

        projectService.deleteProject(1L);
        verify(projectRepository, times(1)).delete(project1);
    }

    @Test
    void testDeleteProject_NotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> projectService.deleteProject(99L));
        assertEquals("Project not found with id: 99", exception.getMessage());
        verify(projectRepository, times(1)).findById(99L);
    }
}
