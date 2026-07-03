package com.learn.tasks.task;

// [STEP 5] Business logic — sits between controller and database (≈ a NestJS @Injectable service).
import com.learn.tasks.exception.NotFoundException;
import com.learn.tasks.task.dto.CreateTaskRequest;
import com.learn.tasks.task.dto.PatchTaskRequest;
import com.learn.tasks.task.dto.TaskResponse;
import com.learn.tasks.task.dto.UpdateTaskRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // @Service → a Spring-managed bean, injected wherever needed
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository repo; // constructor injection (≈ Nest constructor DI)

    public TaskService(TaskRepository repo) { this.repo = repo; }

    // [STEP 5] list with optional filter (status) / search (q) + pagination (page,size,sort)
    public List<TaskResponse> list(TaskStatus status, String q, int page, int size, String sort) {
        log.debug("[STEP 5] service.list status={} q={} page={} size={} sort={}", status, q, page, size, sort);
        List<Task> tasks;
        if (status != null) tasks = repo.findByStatus(status);                      // [STEP 5b] derived query
        else if (q != null && !q.isBlank()) tasks = repo.findByTitleContainingIgnoreCase(q); // [STEP 5b]
        else tasks = repo.findAll(PageRequest.of(page, size, Sort.by(sort))).getContent();    // [STEP 5b] paginated read
        return tasks.stream().map(this::toResponse).toList();
    }

    public TaskResponse get(Long id) {
        log.debug("[STEP 5] service.get id={}", id);
        return toResponse(find(id));
    }

    public TaskResponse create(CreateTaskRequest req) {
        log.debug("[STEP 5] service.create {}", req);
        Task t = new Task();
        t.setTitle(req.title()); t.setDescription(req.description()); t.setStatus(req.status());
        t.setPriority(req.priority()); t.setDueDate(req.dueDate());
        return toResponse(repo.save(t)); // [STEP 5b] INSERT
    }

    public TaskResponse replace(Long id, UpdateTaskRequest req) {
        log.debug("[STEP 5] service.replace id={}", id);
        Task t = find(id);
        t.setTitle(req.title()); t.setDescription(req.description()); t.setStatus(req.status());
        t.setPriority(req.priority()); t.setDueDate(req.dueDate());
        return toResponse(repo.save(t)); // [STEP 5b] UPDATE (all fields replaced)
    }

    public TaskResponse patch(Long id, PatchTaskRequest req) {
        log.debug("[STEP 5] service.patch id={}", id);
        Task t = find(id);
        if (req.title() != null) t.setTitle(req.title());             // only apply the fields that were sent
        if (req.description() != null) t.setDescription(req.description());
        if (req.status() != null) t.setStatus(req.status());
        if (req.priority() != null) t.setPriority(req.priority());
        if (req.dueDate() != null) t.setDueDate(req.dueDate());
        return toResponse(repo.save(t)); // [STEP 5b] UPDATE (partial)
    }

    public void delete(Long id) {
        log.debug("[STEP 5] service.delete id={}", id);
        repo.delete(find(id)); // [STEP 5b] DELETE
    }

    private Task find(Long id) { // shared "load or 404" helper
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Task " + id + " not found")); // -> [STEP 6]
    }

    private TaskResponse toResponse(Task t) { // map entity -> response DTO
        return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getStatus(),
                t.getPriority(), t.getDueDate(), t.getCreatedAt(), t.getUpdatedAt());
    }
}
