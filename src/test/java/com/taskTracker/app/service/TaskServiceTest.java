package com.taskTracker.app.service;

import com.taskTracker.app.model.Project;
import com.taskTracker.app.model.Role;
import com.taskTracker.app.model.Task;
import com.taskTracker.app.model.User;
import com.taskTracker.app.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProjectService projectService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskService taskService;

    private User adminUser;
    private User normalUser;
    private Task task1;
    private Task task2;
    private Project project;

    @BeforeEach
    void setUp() {
        // Mock Users
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.valueOf("ADMIN"));

        normalUser = new User();
        normalUser.setId(2L);
        normalUser.setRole(Role.valueOf("USER"));

        // Mock Project
        project = new Project();
        project.setId(1L);
        project.setName("Project 1");

        // Mock Tasks
        task1 = new Task();
        task1.setId(1L);
        task1.setName("Task 1");
        task1.setUser(adminUser);
        task1.setProject(project);

        task2 = new Task();
        task2.setId(2L);
        task2.setName("Task 2");
        task2.setUser(normalUser);
        task2.setProject(project);
    }

    //   Test Get All Tasks (Admin should get all tasks)
    @Test
    void testGetAllTasks_AdminGetsAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.getAllTasks(adminUser);

        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    //   Test Get All Tasks (User should only get their own)
    @Test
    void testGetAllTasks_UserGetsOwnTasks() {
        when(taskRepository.findByUserId(normalUser.getId())).thenReturn(Arrays.asList(task2));

        List<Task> tasks = taskService.getAllTasks(normalUser);

        assertEquals(1, tasks.size());
        assertEquals(task2.getId(), tasks.get(0).getId());
        verify(taskRepository, times(1)).findByUserId(normalUser.getId());
    }

    //Test Create Task
    @Test
    void testCreateTask_Success() {
        when(authentication.getName()).thenReturn("user1");
        when(userService.getUserByUsername("user1")).thenReturn(normalUser);
        when(projectService.getProjectById(project.getId())).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        Task createdTask = taskService.createTask(task1, authentication);

        assertNotNull(createdTask);
        assertEquals(task1.getName(), createdTask.getName());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    //   Test Update Task
    @Test
    void testUpdateTask_Success() {
        Task updatedTask = new Task();
        updatedTask.setName("Updated Task");
        updatedTask.setDescription("Updated Desc");

        when(taskRepository.findById(task1.getId())).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = taskService.updateTask(task1.getId(), updatedTask);

        assertNotNull(result);
        assertEquals("Updated Task", result.getName());
        assertEquals("Updated Desc", result.getDescription());
    }

    //   Test Update Task (Task not found)
    @Test
    void testUpdateTask_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(99L, new Task()));
    }

    //   Test Delete Task
    @Test
    void testDeleteTask_Success() {
        doNothing().when(taskRepository).deleteById(task1.getId());

        taskService.deleteTask(task1.getId());

        verify(taskRepository, times(1)).deleteById(task1.getId());
    }
}
