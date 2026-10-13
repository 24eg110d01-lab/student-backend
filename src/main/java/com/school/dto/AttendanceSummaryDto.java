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
public class AttendanceSummaryDto {
    private long totalSessions;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    private double attendancePercentage;
    private List<AttendanceDto> records;
}
