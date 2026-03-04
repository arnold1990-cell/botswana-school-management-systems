package com.bosams.web;

import com.bosams.domain.Enums;
import com.bosams.domain.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeController.class)
@AutoConfigureMockMvc(addFilters = false)
class MeControllerTest {
    @Autowired MockMvc mockMvc;

    @Test
    void meReturnsPrincipalData() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        user.setEmail("me@bosams.test");
        user.setFullName("Me User");
        user.setRole(Enums.Role.ADMIN);

        mockMvc.perform(get("/api/me").with(SecurityMockMvcRequestPostProcessors.authentication(new UsernamePasswordAuthenticationToken(user, null, List.of()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("me@bosams.test"));
    }
}
