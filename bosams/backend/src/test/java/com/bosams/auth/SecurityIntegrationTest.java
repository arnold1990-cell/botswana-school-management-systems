package com.bosams.auth;

import com.bosams.domain.AcademicYear;
import com.bosams.domain.Enums;
import com.bosams.domain.UserEntity;
import com.bosams.repository.UserRepository;
import com.bosams.service.AcademicsService;
import com.bosams.testutil.TestConstants;
import com.bosams.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=" + TestConstants.JWT_SECRET,
        "jwt.access-expiration-minutes=15",
        "jwt.refresh-expiration-days=7"
})
@AutoConfigureMockMvc
class SecurityIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired JwtService jwtService;

    @MockBean UserRepository userRepository;
    @MockBean AcademicsService academicsService;

    @Test
    void requestWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/academic-years").contentType("application/json").content("{\"year\":2025}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithInvalidTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/academic-years").header(HttpHeaders.AUTHORIZATION, "Bearer bad.token")
                        .contentType("application/json").content("{\"year\":2025}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validTokenRoleMismatchReturns403() throws Exception {
        UserEntity teacher = TestDataFactory.user(UUID.fromString("33333333-3333-3333-3333-333333333333"), Enums.Role.TEACHER);
        when(userRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        String token = jwtService.generateAccessToken(teacher.getId(), "TEACHER");

        mockMvc.perform(post("/api/academic-years").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType("application/json").content("{\"year\":2025}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void validAdminTokenReturns200() throws Exception {
        UserEntity admin = TestDataFactory.user(TestConstants.USER_ID, Enums.Role.ADMIN);
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(academicsService.createAcademicYear(2025)).thenReturn(TestDataFactory.academicYear(1L, 2025, true));
        String token = jwtService.generateAccessToken(admin.getId(), "ADMIN");

        mockMvc.perform(post("/api/academic-years").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType("application/json").content("{\"year\":2025}"))
                .andExpect(status().isOk());
    }

    @Test
    void subjectsWithoutTokenReturn401() throws Exception {
        mockMvc.perform(get("/api/subjects"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void subjectsWithTeacherTokenReturn200() throws Exception {
        UserEntity teacher = TestDataFactory.user(UUID.fromString("44444444-4444-4444-4444-444444444444"), Enums.Role.TEACHER);
        when(userRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        String token = jwtService.generateAccessToken(teacher.getId(), "TEACHER");

        mockMvc.perform(get("/api/subjects").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }


    @Test
    void subjectsWithAdminTokenReturn200() throws Exception {
        UserEntity admin = TestDataFactory.user(UUID.fromString("77777777-7777-7777-7777-777777777777"), Enums.Role.ADMIN);
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        String token = jwtService.generateAccessToken(admin.getId(), "ADMIN");

        mockMvc.perform(get("/api/subjects").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void subjectsWithStudentTokenReturn403() throws Exception {
        UserEntity student = TestDataFactory.user(UUID.fromString("55555555-5555-5555-5555-555555555555"), Enums.Role.STUDENT);
        when(userRepository.findById(student.getId())).thenReturn(Optional.of(student));
        String token = jwtService.generateAccessToken(student.getId(), "STUDENT");

        mockMvc.perform(get("/api/subjects").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void subjectsWithAccountantTokenReturn403() throws Exception {
        UserEntity accountant = TestDataFactory.user(UUID.fromString("88888888-8888-8888-8888-888888888888"), Enums.Role.ACCOUNTANT);
        when(userRepository.findById(accountant.getId())).thenReturn(Optional.of(accountant));
        String token = jwtService.generateAccessToken(accountant.getId(), "ACCOUNTANT");

        mockMvc.perform(get("/api/subjects").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void learnersWithoutTokenReturn401() throws Exception {
        mockMvc.perform(get("/api/learners"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void learnersWithAdminTokenReturn200() throws Exception {
        UserEntity admin = TestDataFactory.user(UUID.fromString("66666666-6666-6666-6666-666666666666"), Enums.Role.ADMIN);
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        String token = jwtService.generateAccessToken(admin.getId(), "ADMIN");

        mockMvc.perform(get("/api/learners").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }
}
