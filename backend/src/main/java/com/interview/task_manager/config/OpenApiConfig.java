package com.interview.task_manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// [AI assisted chat-002]
// 使用 AI 協助設定 OpenAPI 文件資訊
// 提供 Swagger UI 文件展示
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .description("Fullstack Interview Assignment")
                        .version("1.0.0"));
    }
}