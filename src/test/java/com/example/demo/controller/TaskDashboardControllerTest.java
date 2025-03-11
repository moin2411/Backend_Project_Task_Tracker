package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskDashboardControllerTest {

    @InjectMocks
    private TaskDashboardController taskDashboardController;

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    private User mockUser;
    private List<TaskResponseDTO> mockTaskList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock User
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        // ✅ Mock Project (Fix for NullPointerException)
        Project mockProject = new Project();
        mockProject.setId(1L);
        mockProject.setName("Test Project");

        // ✅ Mock Task Data with Project
        Task mockTask1 = new Task();
        mockTask1.setId(1L);
        mockTask1.setName("Task 1");
        mockTask1.setDescription("Description 1");
        mockTask1.setStatus(TaskStatus.IN_PROGRESS);
        mockTask1.setPriority(TaskPriority.HIGH);
        mockTask1.setDueDate(LocalDate.now());
        mockTask1.setUser(mockUser);
        mockTask1.setProject(mockProject); // ✅ Assign Project

        Task mockTask2 = new Task();
        mockTask2.setId(2L);
        mockTask2.setName("Task 2");
        mockTask2.setDescription("Description 2");
        mockTask2.setStatus(TaskStatus.COMPLETED);
        mockTask2.setPriority(TaskPriority.LOW);
        mockTask2.setDueDate(LocalDate.now());
        mockTask2.setUser(mockUser);
        mockTask2.setProject(mockProject); // ✅ Assign Project

        mockTaskList = Arrays.asList(
                new TaskResponseDTO(mockTask1),
                new TaskResponseDTO(mockTask2)
        );
    }

    @Test
    void testGetUserTasks_Success() {
        when(authentication.getName()).thenReturn(mockUser.getUsername());
        when(userService.getUserByUsername(mockUser.getUsername())).thenReturn(mockUser);
        when(taskService.getUserTasks(mockUser)).thenReturn(mockTaskList);

        List<TaskResponseDTO> result = taskDashboardController.getUserTasks(authentication);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getName());
        assertEquals("Task 2", result.get(1).getName());

        // ✅ FIXED: Corrected assertion to check Project ID instead of Project Name
        assertEquals(1L, result.get(0).getProjectId()); // Project ID is 1
    }

    @Test
    void testGetUserTasks_WhenUserNotAuthenticated_ShouldThrowException() {
        when(authentication.getName()).thenReturn(null);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            taskDashboardController.getUserTasks(authentication);
        });

        assertEquals("User is not authenticated", exception.getMessage());
    }

    @Test
    void testGetUserTasks_WhenNoTasksFound_ShouldReturnEmptyList() {
        when(authentication.getName()).thenReturn(mockUser.getUsername());
        when(userService.getUserByUsername(mockUser.getUsername())).thenReturn(mockUser);
        when(taskService.getUserTasks(mockUser)).thenReturn(Arrays.asList()); // Empty list

        List<TaskResponseDTO> result = taskDashboardController.getUserTasks(authentication);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
