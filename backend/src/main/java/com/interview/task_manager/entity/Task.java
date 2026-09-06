package com.interview.task_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// [AI assisted chat-001]
// 使用 AI 協助建立 Task 領域模型(Entity)
// 包含 JPA Mapping、Lombok 設定與建立/更新時間處理
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Builder.Default
    private Boolean completed = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
}