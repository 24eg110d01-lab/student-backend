package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private String id;
    private String name;
    private String email;
    private String role;
    private String studentId;
    private String teacherId;
}
