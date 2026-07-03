package com.learn.tasks.task.dto;

// PUT body — a FULL replace, so every field is required again (same rules as create).
import com.learn.tasks.task.TaskStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UpdateTaskRequest(
        @NotBlank @Size(max = 120) String title,
        @Size(max = 2000) String description,
        @NotNull TaskStatus status,
        @NotNull @Min(1) @Max(5) Integer priority,
        @FutureOrPresent LocalDate dueDate
) {}
