package com.school.controller;

import com.school.dto.AttendanceDto;
import com.school.dto.AttendanceSummaryDto;
import com.school.dto.BulkAttendanceRequest;
import com.school.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance Management APIs")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Mark attendance in bulk for a course on a specific date")
    public ResponseEntity<List<AttendanceDto>> markBulkAttendance(@Valid @RequestBody BulkAttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.markBulkAttendance(request));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get attendance records for a course (optionally filtered by date)")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByCourse(
            @PathVariable String courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(attendanceService.getAttendanceByCourse(courseId, date));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get attendance summary and records for a student")
    public ResponseEntity<AttendanceSummaryDto> getAttendanceByStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(attendanceService.getAttendanceSummaryByStudent(studentId));
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get student attendance for a specific course")
    public ResponseEntity<AttendanceSummaryDto> getAttendanceByStudentAndCourse(
            @PathVariable String studentId,
            @PathVariable String courseId
    ) {
        return ResponseEntity.ok(attendanceService.getAttendanceSummaryByStudentAndCourse(studentId, courseId));
    }
}
