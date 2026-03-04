package com.bosams.web;

import com.bosams.common.ApiException;
import com.bosams.service.AuthService;
import com.bosams.web.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean AuthService authService;

    @Test
    void loginSuccess200() throws Exception {
        when(authService.login(anyString(), anyString())).thenReturn(new LoginResponse("a","r","Bearer",15L,new LoginResponse.UserSummary(UUID.randomUUID(),"U","u@x.com","ADMIN")));

        mockMvc.perform(post("/api/auth/login").contentType("application/json").content("{\"email\":\"u@x.com\",\"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("a"));
    }

    @Test
    void loginUnauthorized401() throws Exception {
        when(authService.login(anyString(), anyString())).thenThrow(new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_LOGIN", "Invalid"));

        mockMvc.perform(post("/api/auth/login").contentType("application/json").content("{\"email\":\"u@x.com\",\"password\":\"pass\"}"))
                .andExpect(status().isUnauthorized());
    }
}
