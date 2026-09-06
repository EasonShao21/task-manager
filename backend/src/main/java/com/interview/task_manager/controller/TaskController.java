package com.interview.task_manager.controller;

import com.interview.task_manager.entity.Task;
import com.interview.task_manager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

// [AI assisted chat-001]
// 使用 AI 協助建立 RESTful API
// 並加入 OpenAPI(Swagger) 文件註解
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Task API", description = "Task Management APIs")
// [AI assisted chat-001]
public class TaskController {

    private final TaskService taskService;
    @Operation(summary = "取得所有任務")
    @GetMapping
    public List<Task> getTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping
    @Operation(summary = "新增任務")
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢單一任務")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新任務")
    public Task updateTask(
        @PathVariable Long id,
        @RequestBody Task task) {

        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除任務")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}