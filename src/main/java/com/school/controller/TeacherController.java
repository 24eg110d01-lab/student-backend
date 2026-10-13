package com.school.controller;

import com.school.dto.TeacherDto;
import com.school.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "Teacher Management APIs")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get all teachers")
    public ResponseEntity<List<TeacherDto>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get teacher by ID")
    public ResponseEntity<TeacherDto> getTeacherById(@PathVariable String id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create teacher record (Admin only)")
    public ResponseEntity<TeacherDto> createTeacher(@Valid @RequestBody TeacherDto dto) {
        TeacherDto created = teacherService.createTeacher(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update teacher record (Admin only)")
    public ResponseEntity<TeacherDto> updateTeacher(@PathVariable String id, @Valid @RequestBody TeacherDto dto) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete teacher record (Admin only)")
    public ResponseEntity<Void> deleteTeacher(@PathVariable String id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}
