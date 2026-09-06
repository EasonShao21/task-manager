// [AI assisted chat-005]
// 使用 AI 協助封裝 Axios API 呼叫
// 統一管理 Task API 存取方式

import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

export const getTasks = () => api.get("/tasks");

export const createTask = (data) =>
  api.post("/tasks", data);

export const updateTask = (id, data) =>
  api.put(`/tasks/${id}`, data);

export const deleteTask = (id) =>
  api.delete(`/tasks/${id}`);