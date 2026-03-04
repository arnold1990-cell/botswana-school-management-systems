package com.bosams.web;

import com.bosams.domain.ExamSchedule;
import com.bosams.domain.Enums;
import com.bosams.domain.UserEntity;
import com.bosams.repository.ExamGroupRepository;
import com.bosams.repository.ExamScheduleRepository;
import com.bosams.service.AuthorizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExamController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExamControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean ExamScheduleRepository schedules;
    @MockBean ExamGroupRepository groups;
    @MockBean AuthorizationService auth;

    @Test
    void list200ForAdmin() throws Exception {
        UserEntity me = new UserEntity(); me.setRole(Enums.Role.ADMIN);
        when(schedules.findAll()).thenReturn(List.of(new ExamSchedule()));
        mockMvc.perform(get("/api/exam-schedules").with(SecurityMockMvcRequestPostProcessors.authentication(new UsernamePasswordAuthenticationToken(me, null, List.of()))))
                .andExpect(status().isOk());
    }
}
