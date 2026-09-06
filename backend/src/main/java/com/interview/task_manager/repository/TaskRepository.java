package com.interview.task_manager.repository;

import com.interview.task_manager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

// [AI assisted chat-001]
// 使用 AI 協助建立 Spring Data JPA Repository
// 提供 Task 資料庫存取功能
public interface TaskRepository
        extends JpaRepository<Task, Long> {
}