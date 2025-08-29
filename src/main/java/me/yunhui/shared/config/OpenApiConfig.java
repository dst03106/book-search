package me.yunhui.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bookSearchOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Book Search API")
                        .description("도서 검색 및 인기 검색어 API")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Book Search Team")
                                .email("support@booksearch.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발 서버"),
                        new Server()
                                .url("https://api.booksearch.com")
                                .description("운영 서버")
                ));
    }
}
