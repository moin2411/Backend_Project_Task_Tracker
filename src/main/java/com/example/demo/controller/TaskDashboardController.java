package com.example.demo.controller;

import com.example.demo.model.TaskResponseDTO;
import com.example.demo.model.User;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskDashboardController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    //Users & Admins can view their respective tasks
    @Operation(summary = "Get tasks for the authenticated user", description = "Fetches all tasks associated with the logged-in user.")
    @GetMapping("/getTask")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<TaskResponseDTO> getUserTasks(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("User is not authenticated");
        }
        User user = userService.getUserByUsername(authentication.getName());
        return taskService.getUserTasks(user);
    }
}
