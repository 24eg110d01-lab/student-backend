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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "students")
public class Student {

    @Id
    private String id;

    private String userId;

    @Indexed(unique = true)
    private String rollNumber;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDate dob;

    private String gender;

    private String phone;

    private String address;

    private String department;

    private Integer year; // 1, 2, 3, 4

    @Builder.Default
    private List<String> enrolledCourseIds = new ArrayList<>();

    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}
