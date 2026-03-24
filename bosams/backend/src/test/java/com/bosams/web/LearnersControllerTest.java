package com.bosams.web;

import com.bosams.domain.StudentEntity;
import com.bosams.repository.StudentRepository;
import com.bosams.service.AuthorizationService;
import com.bosams.service.StudentManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import com.bosams.domain.Enums;
import com.bosams.domain.UserEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LearnersController.class)
@AutoConfigureMockMvc(addFilters = false)
class LearnersControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean StudentRepository learners;
    @MockBean AuthorizationService auth;
    @MockBean StudentManagementService studentManagementService;

    @Test
    void createBadRequestWhenInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/learners").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void list200() throws Exception {
        when(studentManagementService.list(any(), any(), anyBoolean())).thenReturn(List.of(new StudentEntity()));
        UserEntity u = new UserEntity(); u.setId(UUID.fromString("11111111-1111-1111-1111-111111111111")); u.setRole(Enums.Role.ADMIN);
        mockMvc.perform(get("/api/learners").with(SecurityMockMvcRequestPostProcessors.authentication(new UsernamePasswordAuthenticationToken(u, null, List.of()))))
                .andExpect(status().isOk());
    }

    @Test
    void assignRollNumberBadRequestWhenPayloadInvalid() throws Exception {
        mockMvc.perform(patch("/api/learners/3/roll-number").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
    }
}
