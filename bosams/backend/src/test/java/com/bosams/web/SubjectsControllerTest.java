package com.bosams.web;

import com.bosams.domain.Enums;
import com.bosams.service.SubjectsService;
import com.bosams.web.dto.SubjectDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubjectsController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubjectsControllerTest {
    @Autowired MockMvc mockMvc;

    @MockBean SubjectsService subjectsService;
    @MockBean PasswordEncoder encoder;

    @Test
    void listReturnsDtoPayload() throws Exception {
        when(subjectsService.listSubjects(eq(Enums.SchoolLevel.PRIMARY), eq(2))).thenReturn(List.of(
                new SubjectDto.SubjectResponse(1L, "English", "PRIMARY_ENGLISH", Enums.SchoolLevel.PRIMARY, 1, 4, false)
        ));

        mockMvc.perform(get("/api/subjects").param("level", "PRIMARY").param("grade", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("English"))
                .andExpect(jsonPath("$[0].level").value("PRIMARY"))
                .andExpect(jsonPath("$[0].elective").value(false));
    }
}
