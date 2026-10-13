package com.school.controller;

import com.school.dto.StudentDto;
import com.school.model.StudentStatus;
import com.school.service.CsvExportService;
import com.school.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student Management APIs")
public class StudentController {

    private final StudentService studentService;
    private final CsvExportService csvExportService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get paginated list of students with search and filters")
    public ResponseEntity<Page<StudentDto>> getStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) StudentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rollNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<StudentDto> students = studentService.getAllStudents(search, department, year, status, pageable);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get student details by ID")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable String id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create student record and user account (Admin only)")
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentDto dto) {
        StudentDto created = studentService.createStudent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update student details (Admin only)")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable String id, @Valid @RequestBody StudentDto dto) {
        return ResponseEntity.ok(studentService.updateStudent(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete student record (Admin only)")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Export all students to CSV format")
    public ResponseEntity<byte[]> exportStudentsCsv() {
        byte[] csvData = csvExportService.exportStudentsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
