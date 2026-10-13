package com.school.controller;

import com.school.dto.AttendanceSummaryDto;
import com.school.dto.CourseDto;
import com.school.dto.StudentDto;
import com.school.dto.StudentReportDto;
import com.school.model.User;
import com.school.service.AttendanceService;
import com.school.service.AuthService;
import com.school.service.CourseService;
import com.school.service.GradeService;
import com.school.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student-portal")
@RequiredArgsConstructor
@Tag(name = "Student Portal", description = "Student Self-Service Portal APIs")
public class StudentPortalController {

    private final AuthService authService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final AttendanceService attendanceService;
    private final GradeService gradeService;

    private StudentDto getCurrentStudent() {
        User currentUser = authService.getCurrentUser();
        return studentService.getStudentByUserId(currentUser.getId());
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get current student profile")
    public ResponseEntity<StudentDto> getMyProfile() {
        return ResponseEntity.ok(getCurrentStudent());
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get current student comprehensive report (courses, attendance, GPA)")
    public ResponseEntity<StudentReportDto> getMySummary() {
        StudentDto student = getCurrentStudent();
        return ResponseEntity.ok(gradeService.getStudentReport(student.getId()));
    }

    @GetMapping("/courses")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get courses enrolled by the current student")
    public ResponseEntity<List<CourseDto>> getMyCourses() {
        StudentDto student = getCurrentStudent();
        return ResponseEntity.ok(courseService.getCoursesByStudent(student.getId()));
    }

    @GetMapping("/attendance")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get attendance summary and log for current student")
    public ResponseEntity<AttendanceSummaryDto> getMyAttendance() {
        StudentDto student = getCurrentStudent();
        return ResponseEntity.ok(attendanceService.getAttendanceSummaryByStudent(student.getId()));
    }
}
