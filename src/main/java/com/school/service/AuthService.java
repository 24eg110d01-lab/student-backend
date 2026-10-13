package com.school.service;

import com.school.dto.AuthRequest;
import com.school.dto.AuthResponse;
import com.school.dto.RegisterRequest;
import com.school.exception.BadRequestException;
import com.school.exception.ResourceNotFoundException;
import com.school.model.Role;
import com.school.model.Student;
import com.school.model.Teacher;
import com.school.model.User;
import com.school.repository.StudentRepository;
import com.school.repository.TeacherRepository;
import com.school.repository.UserRepository;
import com.school.security.JwtUtils;
import com.school.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtUtils.generateJwtToken(authentication);

        String studentId = null;
        String teacherId = null;

        if (user.getRole() == Role.ROLE_STUDENT) {
            Optional<Student> student = studentRepository.findByUserId(user.getId());
            if (student.isEmpty()) {
                student = studentRepository.findByEmail(user.getEmail());
            }
            studentId = student.map(Student::getId).orElse(null);
        } else if (user.getRole() == Role.ROLE_TEACHER) {
            Optional<Teacher> teacher = teacherRepository.findByUserId(user.getId());
            if (teacher.isEmpty()) {
                teacher = teacherRepository.findByEmail(user.getEmail());
            }
            teacherId = teacher.map(Teacher::getId).orElse(null);
        }

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .studentId(studentId)
                .teacherId(teacherId)
                .build();
    }

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new BadRequestException("Error: Email is already registered!");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        return userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new ResourceNotFoundException("No authenticated user in context");
        }
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
