package com.bosams.web;

import com.bosams.service.AnnouncementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnnouncementController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnnouncementControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    AnnouncementService announcementService;

    @MockBean
    PasswordEncoder encoder;

    @Test
    void getAll200() throws Exception {
        when(announcementService.all()).thenReturn(List.of());
        mockMvc.perform(get("/api/announcements/all"))
                .andExpect(status().isOk());
    }

    @Test
    void postBadRequest400() throws Exception {
        mockMvc.perform(post("/api/announcements").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
    }
}
