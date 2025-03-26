package com.taskTracker.app.controller;
import com.taskTracker.app.model.Project;
import com.taskTracker.app.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllProjects() throws Exception {
        List<Project> projects = Arrays.asList(
                new Project(1L, "Project A", "Description A",
                        LocalDate.of(2025, 2, 1), LocalDate.of(2025, 6, 1),
                        "IN_PROGRESS", List.of()),

                new Project(2L, "Project B", "Description B",
                        LocalDate.of(2025, 3, 1), LocalDate.of(2025, 7, 1),
                        "NOT_STARTED", List.of())
        );

        when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Project A"))
                .andExpect(jsonPath("$[1].name").value("Project B"));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    @WithMockUser(authorities = "USER")
    void testGetProjectById() throws Exception {
        Project project = new Project(1L, "Project A", "Description A",
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 6, 1),
                "IN_PROGRESS", List.of());

        when(projectService.getProjectById(1L)).thenReturn(project);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Project A"));

        verify(projectService, times(1)).getProjectById(1L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testCreateProject() throws Exception {
        Project project = new Project(1L, "Project A", "Description A",
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 6, 1),
                "IN_PROGRESS", List.of());

        when(projectService.createProject(any(Project.class))).thenReturn(project);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Project A",
                                    "description": "Description A",
                                    "startDate": "2025-02-01",
                                    "endDate": "2025-06-01",
                                    "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Project A"));

        verify(projectService, times(1)).createProject(any(Project.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdateProject() throws Exception {
        Project updatedProject = new Project(1L, "Updated Project", "Updated Description",
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 6, 1),
                "IN_PROGRESS", List.of());

        when(projectService.updateProject(eq(1L), any(Project.class))).thenReturn(updatedProject);

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Updated Project",
                                    "description": "Updated Description",
                                    "startDate": "2025-02-01",
                                    "endDate": "2025-06-01",
                                    "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Project"));

        verify(projectService, times(1)).updateProject(eq(1L), any(Project.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteProject() throws Exception {
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isOk());

        verify(projectService, times(1)).deleteProject(1L);
    }
}
