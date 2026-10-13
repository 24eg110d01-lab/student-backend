package com.school.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "grades")
public class Grade {

    @Id
    private String id;

    private String studentId;

    private String courseId;

    private ExamType examType;

    private Double marksObtained;

    private Double maxMarks;

    private Double percentage;

    private String grade; // e.g. A+, A, B, C, D, F

    private Double gpaPoints; // e.g. 4.0, 3.7, 3.0, 2.0, 1.0, 0.0

    private String semester;

    private String remarks;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
