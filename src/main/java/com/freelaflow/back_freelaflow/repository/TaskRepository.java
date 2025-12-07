package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByKanbanId(Long kanbanId);
}