package com.bosams.web;

import com.bosams.domain.AcademicYear;
import com.bosams.repository.AcademicYearRepository;
import com.bosams.repository.AssessmentTaskRepository;
import com.bosams.repository.TermRepository;
import com.bosams.service.AcademicsService;
import com.bosams.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AcademicsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AcademicsControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean AcademicsService academicsService;
    @MockBean AcademicYearRepository years;
    @MockBean TermRepository terms;
    @MockBean AssessmentTaskRepository tasks;

    @Test
    void createAcademicYear200() throws Exception {
        when(academicsService.createAcademicYear(2025)).thenReturn(TestDataFactory.academicYear(1L, 2025, true));
        mockMvc.perform(post("/api/academic-years").contentType("application/json").content("{\"year\":2025}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2025));
    }

    @Test
    void termsEndpoint200() throws Exception {
        AcademicYear ay = TestDataFactory.academicYear(1L, 2024, true);
        when(years.findByYear(2024)).thenReturn(Optional.of(ay));
        when(terms.findByAcademicYearYearOrderByTermNo(anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/api/terms").param("year", "2024"))
                .andExpect(status().isOk());
    }
}
