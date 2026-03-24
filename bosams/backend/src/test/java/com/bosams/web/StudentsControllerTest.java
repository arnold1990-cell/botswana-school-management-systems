package com.bosams.web;

import com.bosams.domain.Enums;
import com.bosams.domain.StudentEntity;
import com.bosams.domain.UserEntity;
import com.bosams.service.AuthorizationService;
import com.bosams.service.StudentManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentsController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudentsControllerTest {
    @Autowired MockMvc mockMvc;

    @MockBean StudentManagementService studentManagementService;
    @MockBean AuthorizationService auth;

    @Test
    void list200() throws Exception {
        when(studentManagementService.list(any(), any(), anyBoolean())).thenReturn(List.of(new StudentEntity()));
        UserEntity u = new UserEntity();
        u.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        u.setRole(Enums.Role.ADMIN);

        mockMvc.perform(get("/api/students")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(new UsernamePasswordAuthenticationToken(u, null, List.of()))))
                .andExpect(status().isOk());
    }
}
