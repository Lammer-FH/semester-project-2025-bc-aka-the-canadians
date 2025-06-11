package com.campuslostfound.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Campus Lost & Found API")
                .description("API für das Campus Lost & Found System")
                .version("1.0.0"))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080/api")
                    .description("Dev Server")
            ));
    }
}
