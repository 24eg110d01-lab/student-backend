package com.school.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {
    @NotEmpty(message = "Student IDs cannot be empty")
    private List<String> studentIds;
}
