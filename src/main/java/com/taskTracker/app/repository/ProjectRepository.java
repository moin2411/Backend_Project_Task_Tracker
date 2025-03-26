package com.taskTracker.app.repository;

import com.taskTracker.app.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    //Find projects by status
    List<Project> findByStatus(String status);
}
