package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentReportDto {
    private StudentDto student;
    private List<GradeDto> grades;
    private Double gpa;
    private Integer totalCredits;
    private Double attendancePercentage;
    private AttendanceSummaryDto attendanceSummary;
}
