package com.learn.tasks.task.dto;

// PATCH body — a PARTIAL update: every field is optional (null = "don't change this one").
import com.learn.tasks.task.TaskStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PatchTaskRequest(
        @Size(max = 120) String title,             // @Size allows null, only checks when present (no @NotBlank here)
        @Size(max = 2000) String description,
        TaskStatus status,
        @Min(1) @Max(5) Integer priority,
        @FutureOrPresent LocalDate dueDate
) {}
