package com.school.dto;

import com.school.model.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceItem {
    @NotNull(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Attendance status is required")
    private AttendanceStatus status;

    private String remarks;
}
