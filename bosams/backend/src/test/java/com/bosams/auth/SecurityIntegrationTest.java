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
}
