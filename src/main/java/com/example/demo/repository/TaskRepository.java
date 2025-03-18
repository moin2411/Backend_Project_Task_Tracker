package com.example.demo.repository;

import com.example.demo.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    //Find tasks by assigned user ID
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.project WHERE t.user.id = :userId")
    List<Task> findByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.project")
    List<Task> findAll();

    //Find tasks by project ID
    List<Task> findByProjectId(Long projectId);
}
