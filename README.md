# Cardfolio 數位名片管理系統

Cardfolio 是響應式數位名片管理應用，支援名片清單、建立、編輯、刪除、配色與顯示欄位自訂、公開狀態切換，以及可掃描的 QR code 公開名片頁。

## 技術架構

- **Backend**：Java 21、Spring Boot 3、Spring Web、Spring Data JPA、Bean Validation
- **Frontend**：Vue 3、Vite
- **Database**：H2 持久化資料庫（開箱即用）；可透過環境變數切換至 PostgreSQL
- **API contract**：OpenAPI 3.0

## 本機啟動

需求：Java 21、Node.js 20.19+ 或 22.12+、npm。

### 1. 啟動 API

```bash
cd backend
./mvnw spring-boot:run
```

Windows PowerShell：`cd backend; .\mvnw.cmd spring-boot:run`

後端預設使用 `backend/data/cardfolio` 的 H2 檔案資料庫，第一次啟動會建立三張示範名片。要改用 PostgreSQL，設定 `SPRING_DATASOURCE_URL`、`SPRING_DATASOURCE_USERNAME` 和 `SPRING_DATASOURCE_PASSWORD` 環境變數即可。

### 2. 啟動 Vue 前端

```bash
cd frontend
npm install
npm run dev
```

開啟 Vite 顯示的網址（預設 `http://localhost:5173`）。若 API 不在 `http://localhost:8080`，設定前端環境變數 `VITE_API_BASE_URL` 為 API 前綴，例如 `https://api.example.com/api`。

## OpenAPI 規格與 API

- OpenAPI 原始契約：[`backend/src/main/resources/openapi.yaml`](backend/src/main/resources/openapi.yaml)
- 執行中 Swagger UI：<http://localhost:8080/swagger-ui.html>
- 執行中 OpenAPI JSON：<http://localhost:8080/v3/api-docs>
- 執行中 OpenAPI YAML：<http://localhost:8080/v3/api-docs.yaml>

| 方法 | 路徑 | 說明 |
| --- | --- | --- |
| `GET` | `/api/cards` | 列出名片 |
| `POST` | `/api/cards` | 建立名片 |
| `GET` | `/api/cards/{id}` | 取得公開名片（私人或不存在的名片回傳 404） |
| `PUT` | `/api/cards/{id}` | 更新名片資料與外觀設定 |
| `DELETE` | `/api/cards/{id}` | 刪除名片 |

成功建立回傳 `201`，成功刪除回傳 `204`。名片輸入欄位會在後端驗證必填姓名、職稱、Email 格式與欄位長度。

## 領域模型

領域模型原始碼：[`backend/src/main/java/com/interview/task_manager/card/BusinessCard.java`](backend/src/main/java/com/interview/task_manager/card/BusinessCard.java)、[`CardRequest.java`](backend/src/main/java/com/interview/task_manager/card/CardRequest.java)。

| 欄位 | 型別 | 說明 |
| --- | --- | --- |
| `id` | Long | 名片識別碼 |
| `name` | String | 姓名，必填，最多 60 字元 |
| `role` | String | 職稱，必填，最多 80 字元 |
| `company` | String | 公司或品牌 |
| `email` | String | 電子郵件 |
| `phone` | String | 電話 |
| `website` | String | 個人網站或社群連結 |
| `location` | String | 所在地 |
| `color` | String | 背景主題：`violet`、`mint`、`sand` |
| `displayFields` | String[] | 名片正面要顯示的欄位：`email`、`phone`、`website`、`location` |
| `published` | Boolean | 是否公開；公開連結與 QR code 只顯示公開名片 |
| `createdAt` | Instant | 建立時間 |
| `updatedAt` | Instant | 最後更新時間 |

## GitHub

Repository：<https://github.com/EasonShao21/task-manager>

## AI 使用紀錄

本次使用 AI 協助建立名片領域模型、CRUD API 與 OpenAPI 契約，將前端資料接至 API，並撰寫專案操作與領域模型文件。程式碼標註與對話摘要位於 [`chat-records/009.chat`](chat-records/009.chat)。
