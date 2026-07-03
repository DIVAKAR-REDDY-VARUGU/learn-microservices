package com.learn.tasks.task.dto;

// [STEP 4a] Incoming POST body + its validation rules (≈ a NestJS DTO with class-validator).
import com.learn.tasks.task.TaskStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank @Size(max = 120) String title,   // required, non-empty, <= 120 chars
        @Size(max = 2000) String description,      // optional, <= 2000 chars
        @NotNull TaskStatus status,                // required enum: TODO | IN_PROGRESS | DONE
        @NotNull @Min(1) @Max(5) Integer priority, // required, between 1 and 5
        @FutureOrPresent LocalDate dueDate         // optional, today or later
) {}
