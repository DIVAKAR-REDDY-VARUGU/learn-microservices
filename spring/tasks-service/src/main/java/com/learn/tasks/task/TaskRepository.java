package com.learn.tasks.task;

// [STEP 5b] Data layer — extend JpaRepository and you get CRUD for free; method NAMES become SQL (≈ TypeORM repository).
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);            // -> SELECT * FROM tasks WHERE status = ?
    List<Task> findByTitleContainingIgnoreCase(String q);  // -> SELECT * FROM tasks WHERE LOWER(title) LIKE %?%
}
