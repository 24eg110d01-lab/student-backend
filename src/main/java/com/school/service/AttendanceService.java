package com.school.service;

import com.school.dto.AttendanceDto;
import com.school.dto.AttendanceSummaryDto;
import com.school.dto.BulkAttendanceRequest;
import com.school.dto.StudentAttendanceItem;
import com.school.exception.ResourceNotFoundException;
import com.school.model.Attendance;
import com.school.model.AttendanceStatus;
import com.school.model.Course;
import com.school.model.Student;
import com.school.repository.AttendanceRepository;
import com.school.repository.CourseRepository;
import com.school.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public List<AttendanceDto> markBulkAttendance(BulkAttendanceRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + request.getCourseId()));

        List<Attendance> toSave = new ArrayList<>();

        for (StudentAttendanceItem item : request.getAttendances()) {
            Optional<Attendance> existing = attendanceRepository.findByStudentIdAndCourseIdAndDate(
                    item.getStudentId(), course.getId(), request.getDate()
            );

            Attendance attendance;
            if (existing.isPresent()) {
                attendance = existing.get();
                attendance.setStatus(item.getStatus());
                attendance.setRemarks(item.getRemarks());
            } else {
                attendance = Attendance.builder()
                        .studentId(item.getStudentId())
                        .courseId(course.getId())
                        .date(request.getDate())
                        .status(item.getStatus())
                        .remarks(item.getRemarks())
                        .build();
            }
            toSave.add(attendance);
        }

        List<Attendance> saved = attendanceRepository.saveAll(toSave);
        return saved.stream().map(this::toDto).toList();
    }

    public List<AttendanceDto> getAttendanceByCourse(String courseId, LocalDate date) {
        List<Attendance> list;
        if (date != null) {
            list = attendanceRepository.findByCourseIdAndDate(courseId, date);
        } else {
            list = attendanceRepository.findByCourseId(courseId);
        }
        return list.stream().map(this::toDto).toList();
    }

    public AttendanceSummaryDto getAttendanceSummaryByStudent(String studentId) {
        List<Attendance> records = attendanceRepository.findByStudentId(studentId);

        long present = records.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long late = records.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
        long absent = records.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long total = records.size();

        double percentage = 0.0;
        if (total > 0) {
            // Counting present fully and late as 0.5 session
            double weightedPresent = present + (late * 0.5);
            percentage = Math.round((weightedPresent / total) * 1000.0) / 10.0;
        }

        List<AttendanceDto> dtos = records.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .map(this::toDto)
                .toList();

        return AttendanceSummaryDto.builder()
                .totalSessions(total)
                .presentCount(present)
                .lateCount(late)
                .absentCount(absent)
                .attendancePercentage(percentage)
                .records(dtos)
                .build();
    }

    public AttendanceSummaryDto getAttendanceSummaryByStudentAndCourse(String studentId, String courseId) {
        List<Attendance> records = attendanceRepository.findByStudentIdAndCourseId(studentId, courseId);

        long present = records.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long late = records.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
        long absent = records.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long total = records.size();

        double percentage = 0.0;
        if (total > 0) {
            double weightedPresent = present + (late * 0.5);
            percentage = Math.round((weightedPresent / total) * 1000.0) / 10.0;
        }

        return AttendanceSummaryDto.builder()
                .totalSessions(total)
                .presentCount(present)
                .lateCount(late)
                .absentCount(absent)
                .attendancePercentage(percentage)
                .records(records.stream().map(this::toDto).toList())
                .build();
    }

    public AttendanceDto toDto(Attendance attendance) {
        String studentName = "Unknown";
        String rollNumber = "";
        Optional<Student> student = studentRepository.findById(attendance.getStudentId());
        if (student.isPresent()) {
            studentName = student.get().getFullName();
            rollNumber = student.get().getRollNumber();
        }

        String courseName = "Unknown";
        String courseCode = "";
        Optional<Course> course = courseRepository.findById(attendance.getCourseId());
        if (course.isPresent()) {
            courseName = course.get().getName();
            courseCode = course.get().getCode();
        }

        return AttendanceDto.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudentId())
                .studentRollNumber(rollNumber)
                .studentName(studentName)
                .courseId(attendance.getCourseId())
                .courseCode(courseCode)
                .courseName(courseName)
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .remarks(attendance.getRemarks())
                .build();
    }
}
