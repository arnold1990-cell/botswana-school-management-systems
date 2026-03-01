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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class EndToEndWorkflowIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void teacherLoginCreateListLearnerAssignmentAndMarksWorkflow() throws Exception {
        String adminToken = token("admin@bosams.local", "password");
        String teacherToken = token("teacher@bosams.local", "password");

        mockMvc.perform(post("/api/learners")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"admissionNo\":\"ADM-2001\",\"firstName\":\"Lebo\",\"lastName\":\"Kai\",\"gender\":\"FEMALE\",\"gradeLevel\":3}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/learners").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].admissionNo").exists());

        JsonNode year = objectMapper.readTree(mockMvc.perform(get("/api/academic-years/current").header("Authorization", bearer(adminToken))).andReturn().getResponse().getContentAsString());
        JsonNode subject = objectMapper.readTree(mockMvc.perform(get("/api/subjects").header("Authorization", bearer(adminToken))).andReturn().getResponse().getContentAsString()).get(0);
        JsonNode term = objectMapper.readTree(mockMvc.perform(get("/api/terms").param("year", String.valueOf(year.get("year").asInt())).header("Authorization", bearer(adminToken))).andReturn().getResponse().getContentAsString()).get(0);
        JsonNode tasks = objectMapper.readTree(mockMvc.perform(get("/api/tasks").param("termId", term.get("id").asText()).header("Authorization", bearer(adminToken))).andReturn().getResponse().getContentAsString());
        long taskId = tasks.get(0).get("id").asLong();

        mockMvc.perform(post("/api/admin/teacher-assignments")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"teacherUserId\":\"" + me(teacherToken).get("id").asText() + "\",\"academicYearId\":" + year.get("id").asLong() + ",\"gradeLevel\":3,\"subjectId\":" + subject.get("id").asLong() + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gradeLevel").value(3));

        long learnerId = objectMapper.readTree(mockMvc.perform(get("/api/learners").param("gradeLevel", "3").header("Authorization", bearer(adminToken))).andReturn().getResponse().getContentAsString()).get(0).get("id").asLong();

        mockMvc.perform(post("/api/marks/bulk")
                        .header("Authorization", bearer(teacherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subjectId\":"+subject.get("id").asLong()+",\"taskId\":"+taskId+",\"marks\":[{\"learnerId\":"+learnerId+",\"score\":40}]}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/teacher/marks/submit")
                        .param("subjectId", subject.get("id").asText())
                        .param("taskId", String.valueOf(taskId))
                        .param("gradeLevel", "3")
                        .header("Authorization", bearer(teacherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));

        mockMvc.perform(post("/api/marks/bulk")
                        .header("Authorization", bearer(teacherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subjectId\":"+subject.get("id").asLong()+",\"taskId\":"+taskId+",\"marks\":[{\"learnerId\":"+learnerId+",\"score\":39}]}"))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/admin/marks/unlock")
                        .param("subjectId", subject.get("id").asText())
                        .param("taskId", String.valueOf(taskId))
                        .param("gradeLevel", "3")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));

        mockMvc.perform(post("/api/marks/bulk")
                        .header("Authorization", bearer(teacherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subjectId\":"+subject.get("id").asLong()+",\"taskId\":"+taskId+",\"marks\":[{\"learnerId\":"+learnerId+",\"score\":39}]}"))
                .andExpect(status().isOk());
    }

    private JsonNode me(String token) throws Exception {
        return objectMapper.readTree(mockMvc.perform(get("/api/me").header("Authorization", bearer(token))).andReturn().getResponse().getContentAsString());
    }

    private String token(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\""+email+"\",\"password\":\""+password+"\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
