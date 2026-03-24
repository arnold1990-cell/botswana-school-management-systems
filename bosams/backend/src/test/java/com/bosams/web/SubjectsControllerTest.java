package com.bosams.web;

import com.bosams.domain.Enums;
import com.bosams.domain.SubjectEntity;
import com.bosams.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubjectsController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubjectsControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean SubjectRepository subjects;

    @Test
    void list200() throws Exception {
        when(subjects.findByStatusOrderBySchoolLevelAscGradeFromAscNameAsc(Enums.EntityStatus.ACTIVE)).thenReturn(List.of(new SubjectEntity()));
        mockMvc.perform(get("/api/subjects")).andExpect(status().isOk());
    }

    @Test
    void createBadRequest400() throws Exception {
        mockMvc.perform(post("/api/subjects").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete200() throws Exception {
        mockMvc.perform(delete("/api/subjects/1")).andExpect(status().isOk());
    }
}
