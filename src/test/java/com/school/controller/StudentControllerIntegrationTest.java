package com.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.dto.StudentDto;
import com.school.model.StudentStatus;
import com.school.security.JwtUtils;
import com.school.security.UserDetailsServiceImpl;
import com.school.service.CsvExportService;
import com.school.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private CsvExportService csvExportService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStudents_AsAdmin_Returns200() throws Exception {
        StudentDto dto = StudentDto.builder()
                .id("1")
                .rollNumber("CS001")
                .firstName("John")
                .lastName("Doe")
                .email("john@school.com")
                .department("Computer Science")
                .year(1)
                .status(StudentStatus.ACTIVE)
                .build();

        when(studentService.getAllStudents(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(dto)));

        mockMvc.perform(get("/api/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].rollNumber").value("CS001"));
    }

    @Test
    void getStudents_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
