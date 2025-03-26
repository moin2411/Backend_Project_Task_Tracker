package com.taskTracker.app.service;

import com.taskTracker.app.model.User;
import com.taskTracker.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    //Get user by username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    //Save a new user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    //Check if a user exists by ID
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}
