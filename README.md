# Task Manager

全端任務管理系統

## 技術棧

Backend
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- OpenAPI

Frontend
- Vue 3
- Axios
- Tailwind CSS

## 啟動方式

### Backend

cd backend

./mvnw spring-boot:run

### Frontend

cd frontend

npm install

npm run dev

---

## OpenAPI Specification

Swagger UI：

http://localhost:8080/swagger-ui.html

OpenAPI JSON：

http://localhost:8080/v3/api-docs

---

## Domain Model

### Task

| 欄位名稱 | 型別 | 說明 |
|----------|------|------|
| id | Long | 任務編號 |
| title | String | 任務標題 |
| description | String | 任務描述 |
| completed | Boolean | 是否完成 |
| createdAt | LocalDateTime | 建立時間 |
| updatedAt | LocalDateTime | 更新時間 |
