package com.bosams.web;

import com.bosams.domain.Enums;
import com.bosams.domain.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoleDashboardControllerTest {
    @Autowired MockMvc mockMvc;

    @Test
    void teacherDashboard200() throws Exception {
        UserEntity u = new UserEntity(); u.setEmail("t@b.com"); u.setRole(Enums.Role.TEACHER);
        mockMvc.perform(get("/api/teacher/dashboard").with(SecurityMockMvcRequestPostProcessors.authentication(new UsernamePasswordAuthenticationToken(u, null, List.of()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("TEACHER"));
    }
}
