package com.school.dto;

import com.school.model.ExamType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeDto {
    private String id;

    @NotNull(message = "Student ID is required")
    private String studentId;
    private String studentRollNumber;
    private String studentName;

    @NotNull(message = "Course ID is required")
    private String courseId;
    private String courseCode;
    private String courseName;

    @NotNull(message = "Exam type is required")
    private ExamType examType;

    @NotNull(message = "Marks obtained is required")
    @Min(value = 0, message = "Marks cannot be negative")
    private Double marksObtained;

    @NotNull(message = "Max marks is required")
    @Min(value = 1, message = "Max marks must be greater than 0")
    private Double maxMarks;

    private Double percentage;
    private String grade;
    private Double gpaPoints;
    private String semester;
    private String remarks;
}
