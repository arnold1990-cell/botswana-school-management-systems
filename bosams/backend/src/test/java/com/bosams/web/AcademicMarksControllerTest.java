package com.bosams.web;

import com.bosams.service.AcademicMarksService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AcademicMarksController.class)
@AutoConfigureMockMvc(addFilters = false)
class AcademicMarksControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean AcademicMarksService service;

    @Test
    void statusEndpoint200() throws Exception {
        when(service.status(9L, 7L, 5)).thenReturn(new AcademicMarksService.StatusResponse("DRAFT", 0));

        mockMvc.perform(get("/api/marks/status").param("subjectId", "9").param("taskId", "7").param("gradeLevel", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void reportEndpoint200() throws Exception {
        when(service.termReport(anyInt(), anyInt(), anyInt(), anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/api/reports/term").param("year", "2024").param("termNumber", "1").param("gradeLevel", "5").param("subjectId", "9"))
                .andExpect(status().isOk());
    }
}
