package com.school.dto;

import com.school.model.StudentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    private String id;
    private String userId;

    @NotBlank(message = "Roll number is required")
    private String rollNumber;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String password; // optional on update, used to set initial password if provided

    private LocalDate dob;
    private String gender;
    private String phone;
    private String address;

    @NotBlank(message = "Department is required")
    private String department;

    private Integer year;
    private List<String> enrolledCourseIds;
    private StudentStatus status;
}
