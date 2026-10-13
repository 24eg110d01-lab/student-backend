package com.school.controller;

import com.school.dto.GradeDto;
import com.school.dto.StudentReportDto;
import com.school.service.CsvExportService;
import com.school.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Tag(name = "Grades", description = "Grade Recording & GPA Calculation APIs")
public class GradeController {

    private final GradeService gradeService;
    private final CsvExportService csvExportService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Add or update student marks (auto calculates letter grade & GPA points)")
    public ResponseEntity<GradeDto> saveGrade(@Valid @RequestBody GradeDto dto) {
        GradeDto saved = gradeService.saveOrUpdateGrade(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get grades for a student")
    public ResponseEntity<List<GradeDto>> getGradesByStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(gradeService.getGradesByStudent(studentId));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get grades for a course")
    public ResponseEntity<List<GradeDto>> getGradesByCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(gradeService.getGradesByCourse(courseId));
    }

    @GetMapping("/report/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get comprehensive student report card with overall GPA and attendance")
    public ResponseEntity<StudentReportDto> getStudentReport(@PathVariable String studentId) {
        return ResponseEntity.ok(gradeService.getStudentReport(studentId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Delete grade entry")
    public ResponseEntity<Void> deleteGrade(@PathVariable String id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Export all grade results to CSV format")
    public ResponseEntity<byte[]> exportGradesCsv() {
        byte[] csvData = csvExportService.exportGradesCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=grades_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
