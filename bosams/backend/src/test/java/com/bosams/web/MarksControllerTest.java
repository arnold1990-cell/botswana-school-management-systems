package com.bosams.web;

import com.bosams.service.MarksService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MarksController.class)
@AutoConfigureMockMvc(addFilters = false)
class MarksControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean MarksService service;

    @Test
    void getEntryReturns200() throws Exception {
        mockMvc.perform(get("/api/marks/entry/1"))
                .andExpect(status().isOk());
    }

    @Test
    void putDraftReturns200() throws Exception {
        mockMvc.perform(put("/api/marks/entry/1").contentType("application/json").content("[]"))
                .andExpect(status().isOk());
        verify(service).saveDraft(any(), eq(1L), anyList());
    }

    @Test
    void submitReturns200() throws Exception {
        mockMvc.perform(post("/api/marks/submit/1"))
                .andExpect(status().isOk());
        verify(service).submit(any(), eq(1L), isNull());
    }
}
