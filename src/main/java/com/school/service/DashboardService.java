package com.school.service;

import com.school.dto.DashboardStatsDto;
import com.school.model.Attendance;
import com.school.model.AttendanceStatus;
import com.school.model.Grade;
import com.school.model.Student;
import com.school.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;

    public DashboardStatsDto getDashboardStats() {
        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalCourses = courseRepository.count();

        // Calculate average attendance
        List<Attendance> allAttendance = attendanceRepository.findAll();
        double avgAttendance = 0.0;
        if (!allAttendance.isEmpty()) {
            long presentCount = allAttendance.stream()
                    .filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
            long lateCount = allAttendance.stream()
                    .filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
            avgAttendance = Math.round(((presentCount + (lateCount * 0.5)) / allAttendance.size()) * 1000.0) / 10.0;
        }

        // Grade distribution
        List<Grade> allGrades = gradeRepository.findAll();
        Map<String, Long> gradeDistribution = new LinkedHashMap<>();
        gradeDistribution.put("A+", 0L);
        gradeDistribution.put("A", 0L);
        gradeDistribution.put("B+", 0L);
        gradeDistribution.put("B", 0L);
        gradeDistribution.put("C+", 0L);
        gradeDistribution.put("C", 0L);
        gradeDistribution.put("D", 0L);
        gradeDistribution.put("F", 0L);

        for (Grade grade : allGrades) {
            String g = grade.getGrade();
            if (g != null) {
                gradeDistribution.put(g, gradeDistribution.getOrDefault(g, 0L) + 1);
            }
        }

        // Department student counts
        List<Student> allStudents = studentRepository.findAll();
        Map<String, Long> departmentStudentCounts = allStudents.stream()
                .filter(s -> s.getDepartment() != null && !s.getDepartment().isBlank())
                .collect(Collectors.groupingBy(Student::getDepartment, Collectors.counting()));

        // Attendance status counts
        Map<String, Long> attendanceStatusCounts = new LinkedHashMap<>();
        attendanceStatusCounts.put("PRESENT", allAttendance.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count());
        attendanceStatusCounts.put("ABSENT", allAttendance.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count());
        attendanceStatusCounts.put("LATE", allAttendance.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count());

        // Recent 7 days attendance trends
        Map<LocalDate, List<Attendance>> byDate = allAttendance.stream()
                .filter(a -> a.getDate() != null)
                .collect(Collectors.groupingBy(Attendance::getDate));

        List<DashboardStatsDto.DailyAttendanceStat> trends = byDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    long p = entry.getValue().stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
                    long ab = entry.getValue().stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
                    long l = entry.getValue().stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
                    return DashboardStatsDto.DailyAttendanceStat.builder()
                            .date(entry.getKey().toString())
                            .present(p)
                            .absent(ab)
                            .late(l)
                            .build();
                })
                .toList();

        return DashboardStatsDto.builder()
                .totalStudents(totalStudents)
                .totalTeachers(totalTeachers)
                .totalCourses(totalCourses)
                .averageAttendance(avgAttendance)
                .gradeDistribution(gradeDistribution)
                .departmentStudentCounts(departmentStudentCounts)
                .attendanceStatusCounts(attendanceStatusCounts)
                .recentAttendanceTrends(trends)
                .build();
    }
}
