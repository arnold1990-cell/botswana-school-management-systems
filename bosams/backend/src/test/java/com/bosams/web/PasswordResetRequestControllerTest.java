package com.bosams.web;

import com.bosams.domain.PasswordResetRequestEntity;
import com.bosams.service.PasswordResetRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PasswordResetRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class PasswordResetRequestControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean PasswordResetRequestService service;

    @Test
    void create200() throws Exception {
        when(service.create(anyString(), anyString(), any(), any(), anyString())).thenReturn(new PasswordResetRequestEntity());
        mockMvc.perform(post("/api/password-reset-requests")
                        .contentType("application/json")
                        .content("""
                                {
                                  "admissionNo":"ADM-1",
                                  "studentName":"Student One",
                                  "guardianEmail":"guardian@example.com",
                                  "guardianPhone":"+26770000000",
                                  "reason":"Forgot password"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void create400WhenPhoneInvalid() throws Exception {
        mockMvc.perform(post("/api/password-reset-requests")
                        .contentType("application/json")
                        .content("{" +
                                "\"admissionNo\":\"ADM-1\"," +
                                "\"studentName\":\"Student One\"," +
                                "\"guardianPhone\":\"invalid\"," +
                                "\"reason\":\"Forgot\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void list200() throws Exception {
        when(service.list()).thenReturn(List.of(new PasswordResetRequestEntity()));
        mockMvc.perform(get("/api/password-reset-requests"))
                .andExpect(status().isOk());
    }

    @Test
    void patch200() throws Exception {
        when(service.updateStatus(eq(9L), any(), any())).thenReturn(new PasswordResetRequestEntity());
        mockMvc.perform(patch("/api/password-reset-requests/9")
                        .contentType("application/json")
                        .content("{" +
                                "\"status\":\"APPROVED\"," +
                                "\"adminNote\":\"Reset generated\"}"))
                .andExpect(status().isOk());
    }
}
