package com.bosams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthLoginIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void adminLoginReturnsJwtAndCanAccessProtectedEndpoint() throws Exception {
        String accessToken = login("admin@bosams.local", "password", "ADMIN");

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@bosams.local"));

        mockMvc.perform(get("/api/subjects")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }


    @Test
    void validTokensReturn200ForProtectedAcademicEndpoints() throws Exception {
        String adminToken = login("admin@bosams.local", "password", "ADMIN");
        String teacherToken = login("teacher@bosams.local", "password", "TEACHER");

        String yearBody = mockMvc.perform(get("/api/academic-years/current")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int year = objectMapper.readTree(yearBody).get("year").asInt();

        mockMvc.perform(get("/api/subjects")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/learners")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/terms")
                        .param("year", String.valueOf(year))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/teacher/my-assignments")
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk());
    }

    @Test
    void teacherLoginCanAccessTeacherEndpointButNotAdminEndpoint() throws Exception {
        String accessToken = login("teacher@bosams.local", "password", "TEACHER");


        String tokenPayload = decodeJwtPayload(accessToken);
        assertTrue(tokenPayload.contains("\"role\":\"TEACHER\""));

        mockMvc.perform(get("/api/teacher/dashboard")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("TEACHER"));

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/subjects")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    private String login(String email, String password, String expectedRole) throws Exception {
        String loginBody = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        String loginResponseBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.user.role").value(expectedRole))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginResponseBody);
        return loginJson.get("accessToken").asText();
    }
    private String decodeJwtPayload(String jwt) {
        String[] parts = jwt.split("\\.");
        byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
