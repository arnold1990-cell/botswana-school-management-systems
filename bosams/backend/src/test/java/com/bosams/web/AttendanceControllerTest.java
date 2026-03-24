package com.bosams.web;

import com.bosams.service.AttendanceService;
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

@WebMvcTest(AttendanceController.class)
@AutoConfigureMockMvc(addFilters = false)
class AttendanceControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    AttendanceService attendanceService;

    @MockBean
    PasswordEncoder encoder;

    @Test
    void grade200() throws Exception {
        when(attendanceService.gradeAttendance(3, null)).thenReturn(List.of());
        mockMvc.perform(get("/api/attendance/grade/3"))
                .andExpect(status().isOk());
    }

    @Test
    void markBadRequest400() throws Exception {
        mockMvc.perform(post("/api/attendance/mark").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
    }
}
