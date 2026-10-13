package com.school.service;

import com.school.dto.TeacherDto;
import com.school.exception.BadRequestException;
import com.school.exception.ResourceNotFoundException;
import com.school.model.Course;
import com.school.model.Role;
import com.school.model.Teacher;
import com.school.model.User;
import com.school.repository.CourseRepository;
import com.school.repository.TeacherRepository;
import com.school.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public List<TeacherDto> getAllTeachers() {
        return teacherRepository.findAll().stream().map(this::toDto).toList();
    }

    public TeacherDto getTeacherById(String id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));
        return toDto(teacher);
    }

    public TeacherDto getTeacherByUserId(String userId) {
        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with user ID: " + userId));
        return toDto(teacher);
    }

    public TeacherDto createTeacher(TeacherDto dto) {
        if (teacherRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("Email already exists: " + dto.getEmail());
        }

        User user = null;
        if (StringUtils.hasText(dto.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(dto.getEmail().trim().toLowerCase());
            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                String rawPassword = StringUtils.hasText(dto.getPassword()) ? dto.getPassword() : "Teacher@123";
                user = User.builder()
                        .name(dto.getName().trim())
                        .email(dto.getEmail().trim().toLowerCase())
                        .password(passwordEncoder.encode(rawPassword))
                        .role(Role.ROLE_TEACHER)
                        .build();
                user = userRepository.save(user);
            }
        }

        Teacher teacher = Teacher.builder()
                .userId(user != null ? user.getId() : dto.getUserId())
                .name(dto.getName().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .department(dto.getDepartment().trim())
                .phone(dto.getPhone())
                .courseIds(dto.getCourseIds() != null ? dto.getCourseIds() : new ArrayList<>())
                .build();

        Teacher saved = teacherRepository.save(teacher);
        return toDto(saved);
    }

    public TeacherDto updateTeacher(String id, TeacherDto dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));

        teacher.setName(dto.getName().trim());
        teacher.setDepartment(dto.getDepartment().trim());
        teacher.setPhone(dto.getPhone());
        if (dto.getCourseIds() != null) {
            teacher.setCourseIds(dto.getCourseIds());
        }

        if (StringUtils.hasText(dto.getEmail()) && !dto.getEmail().equalsIgnoreCase(teacher.getEmail())) {
            teacher.setEmail(dto.getEmail().trim().toLowerCase());
            if (teacher.getUserId() != null) {
                userRepository.findById(teacher.getUserId()).ifPresent(u -> {
                    u.setEmail(dto.getEmail().trim().toLowerCase());
                    u.setName(teacher.getName());
                    userRepository.save(u);
                });
            }
        }

        Teacher updated = teacherRepository.save(teacher);
        return toDto(updated);
    }

    public void deleteTeacher(String id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));

        // Dissociate courses
        List<Course> courses = courseRepository.findByTeacherId(id);
        for (Course course : courses) {
            course.setTeacherId(null);
            courseRepository.save(course);
        }

        if (teacher.getUserId() != null) {
            userRepository.deleteById(teacher.getUserId());
        }

        teacherRepository.delete(teacher);
    }

    public TeacherDto toDto(Teacher teacher) {
        return TeacherDto.builder()
                .id(teacher.getId())
                .userId(teacher.getUserId())
                .name(teacher.getName())
                .email(teacher.getEmail())
                .department(teacher.getDepartment())
                .phone(teacher.getPhone())
                .courseIds(teacher.getCourseIds() != null ? teacher.getCourseIds() : new ArrayList<>())
                .build();
    }
}
