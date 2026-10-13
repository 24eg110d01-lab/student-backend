package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private long totalStudents;
    private long totalTeachers;
    private long totalCourses;
    private double averageAttendance;
    private Map<String, Long> gradeDistribution;
    private Map<String, Long> departmentStudentCounts;
    private Map<String, Long> attendanceStatusCounts;
    private List<DailyAttendanceStat> recentAttendanceTrends;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyAttendanceStat {
        private String date;
        private long present;
        private long absent;
        private long late;
    }
}
