package com.school.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "attendance")
@CompoundIndex(name = "student_course_date_idx", def = "{'studentId': 1, 'courseId': 1, 'date': 1}", unique = true)
public class Attendance {

    @Id
    private String id;

    private String studentId;

    private String courseId;

    private LocalDate date;

    private AttendanceStatus status;

    private String remarks;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
