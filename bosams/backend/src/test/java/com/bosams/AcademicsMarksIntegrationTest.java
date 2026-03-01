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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AcademicsMarksIntegrationTest {
    @Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    @DynamicPropertySource static void props(DynamicPropertyRegistry r){
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void seededSubjectsExist() throws Exception {
        mockMvc.perform(get("/api/subjects").header("Authorization", "Bearer " + token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)));
    }

    @Test
    void creatingAcademicYearCreatesTermsAndTasks() throws Exception {
        mockMvc.perform(post("/api/academic-years")
                        .header("Authorization", "Bearer " + token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"year\":2031}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2031));

        String termsBody = mockMvc.perform(get("/api/terms").param("year", "2031").header("Authorization", "Bearer " + token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andReturn().getResponse().getContentAsString();

        JsonNode firstTerm = objectMapper.readTree(termsBody).get(0);
        mockMvc.perform(get("/api/tasks")
                        .param("termId", firstTerm.get("id").asText())
                        .header("Authorization", "Bearer " + token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void bulkMarksSaveStoresComputedGradeAndReportTotals() throws Exception {
        String auth = "Bearer " + token();
        mockMvc.perform(post("/api/learners").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"admissionNo\":\"ADM-1001\",\"firstName\":\"Neo\",\"lastName\":\"Kabelo\",\"gender\":\"MALE\",\"gradeLevel\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));

        Long learnerId = objectMapper.readTree(mockMvc.perform(get("/api/learners").param("gradeLevel", "5").header("Authorization", auth))
                .andReturn().getResponse().getContentAsString()).get(0).get("id").asLong();

        JsonNode currentYear = objectMapper.readTree(mockMvc.perform(get("/api/academic-years/current").header("Authorization", auth)).andReturn().getResponse().getContentAsString());
        int year = currentYear.get("year").asInt();
        JsonNode term = objectMapper.readTree(mockMvc.perform(get("/api/terms").param("year", String.valueOf(year)).header("Authorization", auth)).andReturn().getResponse().getContentAsString()).get(0);
        JsonNode tasks = objectMapper.readTree(mockMvc.perform(get("/api/tasks").param("termId", term.get("id").asText()).header("Authorization", auth)).andReturn().getResponse().getContentAsString());
        long catTask = tasks.get(0).get("type").asText().equals("CAT") ? tasks.get(0).get("id").asLong() : tasks.get(1).get("id").asLong();
        long examTask = tasks.get(0).get("type").asText().equals("EXAM") ? tasks.get(0).get("id").asLong() : tasks.get(1).get("id").asLong();
        long subjectId = objectMapper.readTree(mockMvc.perform(get("/api/subjects").header("Authorization", auth)).andReturn().getResponse().getContentAsString()).get(0).get("id").asLong();

        mockMvc.perform(post("/api/marks/bulk").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subjectId\":"+subjectId+",\"taskId\":"+catTask+",\"marks\":[{\"learnerId\":"+learnerId+",\"score\":42}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].gradeLetter").value("A"));

        mockMvc.perform(post("/api/marks/bulk").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subjectId\":"+subjectId+",\"taskId\":"+examTask+",\"marks\":[{\"learnerId\":"+learnerId+",\"score\":38}]}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reports/term")
                        .header("Authorization", auth)
                        .param("year", String.valueOf(year))
                        .param("termNumber", "1")
                        .param("gradeLevel", "5")
                        .param("subjectId", String.valueOf(subjectId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].catScore").value(42))
                .andExpect(jsonPath("$[0].examScore").value(38))
                .andExpect(jsonPath("$[0].total").value(80));
    }

    private String token() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@bosams.local\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }
}
