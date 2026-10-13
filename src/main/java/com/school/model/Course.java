package com.school.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class Course {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    private String name;

    private Integer credits;

    private String department;

    private String teacherId; // References Teacher id

    private String semester; // e.g., "Fall 2026", "Spring 2026"

    @Builder.Default
    private List<String> enrolledStudentIds = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
