package com.school.dto;

import com.school.model.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {
    private String id;
    private String studentId;
    private String studentRollNumber;
    private String studentName;
    private String courseId;
    private String courseCode;
    private String courseName;
    private LocalDate date;
    private AttendanceStatus status;
    private String remarks;
}
