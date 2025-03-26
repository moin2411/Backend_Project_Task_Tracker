package com.taskTracker.app.controller;

import com.taskTracker.app.model.Project;
import com.taskTracker.app.model.Task;
import com.taskTracker.app.model.TaskDTO;
import com.taskTracker.app.model.User;
import com.taskTracker.app.repository.ProjectRepository;
import com.taskTracker.app.repository.UserRepository;
import com.taskTracker.app.service.TaskService;
import com.taskTracker.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    //Admins can view all tasks
    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<Task> getAllTasks(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return taskService.getAllTasks(user);
    }

    //Only Admins can update tasks
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    //Only Admins can delete tasks
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    //Admin and User can create the task
    @PostMapping("/createTask")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Task> createTask(@RequestBody TaskDTO taskDTO) {
        //Fetch User and Project from DB
        User user = userRepository.findById(taskDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));

        //Create Task Entity
        Task task = new Task();
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setDueDate(taskDTO.getDueDate());
        task.setUser(user);
        task.setProject(project);
        task.setVersion(taskDTO.getVersion());
        //Save Task
        Task savedTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }
}
