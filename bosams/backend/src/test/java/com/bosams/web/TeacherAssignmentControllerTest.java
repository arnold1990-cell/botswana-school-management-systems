package com.bosams.web;

import com.bosams.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeacherAssignmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class TeacherAssignmentControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean UserRepository users;
    @MockBean TeacherAssignmentRepository assignments;
    @MockBean AcademicYearRepository years;
    @MockBean StreamRepository streams;
    @MockBean SubjectRepository subjects;
    @MockBean PasswordEncoder encoder;

    @Test
    void listTeachers200() throws Exception {
        when(users.findByRole(any())).thenReturn(List.of());
        mockMvc.perform(get("/api/admin/teachers")).andExpect(status().isOk());
    }

    @Test
    void createTeacherBadRequest400() throws Exception {
        mockMvc.perform(post("/api/admin/teachers").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
    }
}
