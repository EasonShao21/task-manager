package com.interview.task_manager.service;

import com.interview.task_manager.entity.Task;
import com.interview.task_manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// [AI assisted chat-001]
// 使用 AI 協助產生 Task CRUD 商業邏輯
// 包含查詢、新增、修改與刪除功能
@Service
@RequiredArgsConstructor
// [AI assisted chat-001]
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }
    
    public Task updateTask(Long id, Task updatedTask) {

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setCompleted(updatedTask.getCompleted());

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}