package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskController taskController;

    private User adminUser;
    private User normalUser;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole(Role.valueOf("ADMIN"));

        normalUser = new User();
        normalUser.setId(2L);
        normalUser.setUsername("user");
        normalUser.setRole(Role.valueOf("USER"));

        project = new Project();
        project.setId(1L);
        project.setName("Sample Project");

        task = new Task();
        task.setId(1L);
        task.setName("Sample Task");
        task.setDescription("Task Description");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(TaskPriority.HIGH);
        task.setDueDate(LocalDate.now().plusDays(5));
        task.setUser(adminUser);
        task.setProject(project);
    }

    // Test: Create Task (Admin or User)
    @Test
    void testCreateTask_Success() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("New Task");
        taskDTO.setDescription("Task Description");
        taskDTO.setStatus(TaskStatus.IN_PROGRESS);
        taskDTO.setPriority(TaskPriority.MEDIUM);
        taskDTO.setDueDate(LocalDate.now().plusDays(5));
        taskDTO.setUserId(1L);
        taskDTO.setProjectId(1L);

        when(userRepository.findById(taskDTO.getUserId())).thenReturn(Optional.of(adminUser));
        when(projectRepository.findById(taskDTO.getProjectId())).thenReturn(Optional.of(project));
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        ResponseEntity<Task> response = taskController.createTask(taskDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Sample Task", response.getBody().getName());
    }

    //   Test: Get All Tasks (Admin)
    @Test
    void testGetAllTasks_Admin() {
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(taskService.getAllTasks(adminUser)).thenReturn(Arrays.asList(task));

        assertEquals(1, taskController.getAllTasks(authentication).size());
    }

    //   Test: Get All Tasks (User)
    @Test
    void testGetAllTasks_User() {
        when(authentication.getName()).thenReturn("user");
        when(userService.getUserByUsername("user")).thenReturn(normalUser);
        when(taskService.getAllTasks(normalUser)).thenReturn(Arrays.asList());

        assertEquals(0, taskController.getAllTasks(authentication).size());
    }

    //   Test: Delete Task (Admin)
    @Test
    void testDeleteTask_Admin() {
        doNothing().when(taskService).deleteTask(1L);

        taskController.deleteTask(1L);
        verify(taskService, times(1)).deleteTask(1L);
    }
}
