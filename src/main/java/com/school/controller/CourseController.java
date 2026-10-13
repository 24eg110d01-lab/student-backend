package com.school.controller;

import com.school.dto.CourseDto;
import com.school.dto.EnrollmentRequest;
import com.school.dto.StudentDto;
import com.school.service.CourseService;
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
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course Catalog & Enrollment APIs")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get all courses")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable String id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get courses taught by teacher ID")
    public ResponseEntity<List<CourseDto>> getCoursesByTeacher(@PathVariable String teacherId) {
        return ResponseEntity.ok(courseService.getCoursesByTeacher(teacherId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new course (Admin only)")
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto dto) {
        CourseDto created = courseService.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update course details (Admin only)")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable String id, @Valid @RequestBody CourseDto dto) {
        return ResponseEntity.ok(courseService.updateCourse(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete course (Admin only)")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Enroll students into course")
    public ResponseEntity<CourseDto> enrollStudents(
            @PathVariable String id,
            @Valid @RequestBody EnrollmentRequest request
    ) {
        return ResponseEntity.ok(courseService.enrollStudents(id, request.getStudentIds()));
    }

    @PostMapping("/{id}/drop/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Drop a student from course")
    public ResponseEntity<CourseDto> dropStudent(
            @PathVariable String id,
            @PathVariable String studentId
    ) {
        return ResponseEntity.ok(courseService.dropStudent(id, studentId));
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get students enrolled in course")
    public ResponseEntity<List<StudentDto>> getEnrolledStudents(@PathVariable String id) {
        return ResponseEntity.ok(courseService.getEnrolledStudents(id));
    }
}
