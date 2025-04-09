package com.dentner.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

  @Bean
  OpenAPI openAPI() {
    return new OpenAPI()
    .info(new Info()
    .title("덴트너 API")
    .description("")
    .version("1.0.0"));
  }
}
