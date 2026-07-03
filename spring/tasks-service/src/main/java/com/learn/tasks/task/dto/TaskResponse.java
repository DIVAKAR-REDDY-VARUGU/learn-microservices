package com.learn.tasks.task.dto;

// Outgoing shape returned to the client — keeps the JPA entity out of the API (≈ a response DTO).
import com.learn.tasks.task.TaskStatus;
import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        Integer priority,
        LocalDate dueDate,
        Instant createdAt,
        Instant updatedAt
) {}
