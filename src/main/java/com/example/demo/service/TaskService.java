package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.model.TaskResponseDTO;
import com.example.demo.model.User;
import com.example.demo.model.Project;
import com.example.demo.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    //Get tasks (Admins get all, Users get their own tasks)
    public List<Task> getAllTasks(User user) {
        if (user.getRole().name().equals("ADMIN")) {
            return taskRepository.findAll();
        } else {
            return taskRepository.findByUserId(user.getId());
        }
    }

    //Get a specific task (Admins can access all, Users only their own tasks)
    public List<Task> getTaskForUserOrAdmin(Long id, User user) {
        List<Task> task = taskRepository.findByUserId(id);
        return task;
              //  .orElseThrow(() -> new RuntimeException("Task not found with id:-" + id));

    }


    public Task createTask(Task task, Authentication authentication) {
        //Get the authenticated user
        User user = userService.getUserByUsername(authentication.getName());

        //Validate and fetch Project
        Project project = projectService.getProjectById(task.getProject().getId());

        //Set the user and project before saving
        task.setUser(user);
        task.setProject(project);

        return taskRepository.save(task);
    }



    @Transactional //Ensures transaction consistency
    public Task updateTask(Long taskId, Task updatedTask) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));

        existingTask.setName(updatedTask.getName());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setVersion(updatedTask.getVersion());
        //Hibernate will check version before updating
        return taskRepository.save(existingTask);
    }

    //Only Admins can delete tasks
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    //Get task by ID
    private Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }
    public List<TaskResponseDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream().map(TaskResponseDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public Task createTask(Task task) {
        if (task.getId() != null) { // Ensure new entity creation
            throw new IllegalArgumentException("Task ID should be null when creating a new task");
        }
        return taskRepository.save(task);
    }

    public List<TaskResponseDTO> getUserTasks(User user) {
        List<Task> tasks;
        tasks = taskRepository.findByUserId(user.getId());
        return tasks.stream().map(TaskResponseDTO::new).collect(Collectors.toList());
    }

}
