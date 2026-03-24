package com.bosams;

import com.bosams.auth.JwtService;
import com.bosams.domain.SubjectEntity;
import com.bosams.domain.Term;
import com.bosams.repository.SubjectRepository;
import com.bosams.repository.TermRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class StartupSeedIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private TermRepository termRepository;
    @Autowired private JwtService jwtService;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void startupChainLoadsCoreBeans() {
        assertThat(jwtService).isNotNull();
        assertThat(subjectRepository).isNotNull();
        assertThat(termRepository).isNotNull();
    }

    @Test
    void flywayCreatesSubjectCodeColumnWithSafeLengthAndSeedsLongCodes() {
        Integer maxLength = jdbcTemplate.queryForObject(
                "SELECT character_maximum_length FROM information_schema.columns WHERE table_name='subject' AND column_name='code'",
                Integer.class
        );
        assertThat(maxLength).isGreaterThanOrEqualTo(100);

        SubjectEntity environmentalScience = subjectRepository.findAll().stream()
                .filter(subject -> "PRIMARY_ENVIRONMENTAL_SCIENCE".equals(subject.getCode()))
                .findFirst()
                .orElseThrow();
        assertThat(environmentalScience.getName()).isEqualTo("Environmental Science");
    }

    @Test
    void subjectsAndTermsApisReturnSeededAcademicData() throws Exception {
        String auth = "Bearer " + token();

        String subjectsJson = mockMvc.perform(get("/api/subjects").header("Authorization", auth))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<?> subjects = objectMapper.readValue(subjectsJson, List.class);
        assertThat(subjects).isNotEmpty();

        int currentYear = objectMapper.readTree(mockMvc.perform(get("/api/academic-years/current").header("Authorization", auth))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString())
                .get("year")
                .asInt();

        mockMvc.perform(get("/api/terms").param("year", String.valueOf(currentYear)).header("Authorization", auth))
                .andExpect(status().isOk());

        List<Term> terms = termRepository.findByAcademicYearYearOrderByTermNo(currentYear);
        assertThat(terms).hasSize(3);
        assertThat(terms.stream().map(Term::getTermNo).sorted(Comparator.naturalOrder()).toList())
                .containsExactly(1, 2, 3);
    }

    private String token() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"admin@bosams.local\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }
}
