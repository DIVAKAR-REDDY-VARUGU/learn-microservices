package com.learn.tasks.task;

// [DB] JPA entity — one row in the "tasks" table (≈ a TypeORM @Entity).
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment primary key
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING) // store the enum's NAME ("TODO") as text, not its number
    @Column(nullable = false)
    private TaskStatus status;

    @Column(nullable = false)
    private Integer priority;

    private LocalDate dueDate;

    @CreationTimestamp // Hibernate sets this once, on insert
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp // Hibernate sets this on every update
    private Instant updatedAt;

    public Task() {} // JPA (and our seeder) need a no-arg constructor

    // getters & setters — JPA and our entity<->DTO mapping use these
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
