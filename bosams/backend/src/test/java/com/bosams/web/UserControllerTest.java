package com.bosams.web;

import com.bosams.domain.Enums;
import com.bosams.domain.UserEntity;
import com.bosams.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean UserRepository users;
    @MockBean PasswordEncoder encoder;

    @Test
    void listForbiddenForNonAdmin() throws Exception {
        UserEntity me = new UserEntity(); me.setId(UUID.randomUUID()); me.setRole(Enums.Role.TEACHER);
        mockMvc.perform(get("/api/users").with(SecurityMockMvcRequestPostProcessors.authentication(new UsernamePasswordAuthenticationToken(me, null, List.of()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOkForAdmin() throws Exception {
        UserEntity me = new UserEntity(); me.setId(UUID.randomUUID()); me.setRole(Enums.Role.ADMIN);
        when(users.findAll()).thenReturn(List.of(new UserEntity()));
        mockMvc.perform(get("/api/users").with(SecurityMockMvcRequestPostProcessors.authentication(new UsernamePasswordAuthenticationToken(me, null, List.of()))))
                .andExpect(status().isOk());
    }
}
