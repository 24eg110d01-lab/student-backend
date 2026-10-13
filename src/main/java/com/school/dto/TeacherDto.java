package com.school.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto {
    private String id;
    private String userId;

    @NotBlank(message = "Teacher name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String password;

    @NotBlank(message = "Department is required")
    private String department;

    private String phone;
    private List<String> courseIds;
}
