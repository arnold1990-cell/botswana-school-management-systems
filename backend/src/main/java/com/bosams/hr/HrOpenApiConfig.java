package com.bosams.hr;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class HrOpenApiConfig {
    @Bean
    GroupedOpenApi hrApiGroup() {
        return GroupedOpenApi.builder().group("HR").pathsToMatch("/api/v1/hr/**").build();
    }
}
