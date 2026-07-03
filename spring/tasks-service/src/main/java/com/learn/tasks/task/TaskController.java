package com.learn.tasks.task;

// [STEP 4] HTTP layer — maps URLs to methods, binds arguments, returns data (≈ a NestJS @Controller). No business logic.
import com.learn.tasks.task.dto.CreateTaskRequest;
import com.learn.tasks.task.dto.PatchTaskRequest;
import com.learn.tasks.task.dto.TaskResponse;
import com.learn.tasks.task.dto.UpdateTaskRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController                // = Nest @Controller (REST flavor: return value is auto-serialized to JSON)
@RequestMapping("/api/tasks")  // base path shared by every method below
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }

    // GET /api/tasks?status=TODO&q=abc&page=0&size=10&sort=id  — QUERY PARAMS, all optional with defaults
    @GetMapping
    public List<TaskResponse> list(
            @RequestParam(required = false) TaskStatus status,   // ?status=TODO  (auto-converted to the enum)
            @RequestParam(required = false) String q,            // ?q=text       (title search)
            @RequestParam(defaultValue = "0") int page,          // ?page=0
            @RequestParam(defaultValue = "10") int size,         // ?size=10
            @RequestParam(defaultValue = "id") String sort) {    // ?sort=id
        log.info("[STEP 4] GET /api/tasks");
        return service.list(status, q, page, size, sort);
    }

    // GET /api/tasks/{id}  — PATH VARIABLE
    @GetMapping("/{id}")
    public TaskResponse getOne(@PathVariable Long id) {
        log.info("[STEP 4] GET /api/tasks/{}", id);
        return service.get(id);
    }

    // POST /api/tasks  — @Valid @RequestBody (BODY) + @RequestHeader (HEADER) -> 201 Created with Location
    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @Valid @RequestBody CreateTaskRequest req,                                   // [STEP 4a] validated BEFORE this runs
            @RequestHeader(value = "X-Request-Source", defaultValue = "unknown") String source, // a request header
            UriComponentsBuilder uri) {
        log.info("[STEP 4] POST /api/tasks (X-Request-Source={})", source);
        TaskResponse created = service.create(req);
        URI location = uri.path("/api/tasks/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created); // 201 + "Location: /api/tasks/{id}" header
    }

    // PUT /api/tasks/{id}  — FULL replace
    @PutMapping("/{id}")
    public TaskResponse replace(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest req) {
        log.info("[STEP 4] PUT /api/tasks/{}", id);
        return service.replace(id, req);
    }

    // PATCH /api/tasks/{id}  — PARTIAL update
    @PatchMapping("/{id}")
    public TaskResponse patch(@PathVariable Long id, @Valid @RequestBody PatchTaskRequest req) {
        log.info("[STEP 4] PATCH /api/tasks/{}", id);
        return service.patch(id, req);
    }

    // DELETE /api/tasks/{id}  — 204 No Content
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // sets the success status code on this method
    public void delete(@PathVariable Long id) {
        log.info("[STEP 4] DELETE /api/tasks/{}", id);
        service.delete(id);
    }
}
