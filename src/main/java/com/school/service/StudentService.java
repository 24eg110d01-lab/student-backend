package com.school.service;

import com.school.dto.StudentDto;
import com.school.exception.BadRequestException;
import com.school.exception.ResourceNotFoundException;
import com.school.model.Course;
import com.school.model.Role;
import com.school.model.Student;
import com.school.model.StudentStatus;
import com.school.model.User;
import com.school.repository.CourseRepository;
import com.school.repository.StudentRepository;
import com.school.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;

    public Page<StudentDto> getAllStudents(String search, String department, Integer year, StudentStatus status, Pageable pageable) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (StringUtils.hasText(search)) {
            String regex = ".*" + search.trim() + ".*";
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("firstName").regex(regex, "i"),
                    Criteria.where("lastName").regex(regex, "i"),
                    Criteria.where("rollNumber").regex(regex, "i"),
                    Criteria.where("email").regex(regex, "i")
            ));
        }

        if (StringUtils.hasText(department)) {
            criteriaList.add(Criteria.where("department").is(department.trim()));
        }

        if (year != null) {
            criteriaList.add(Criteria.where("year").is(year));
        }

        if (status != null) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, Student.class);
        query.with(pageable);
        List<Student> students = mongoTemplate.find(query, Student.class);

        List<StudentDto> dtos = students.stream().map(this::toDto).toList();
        return new PageImpl<>(dtos, pageable, total);
    }

    public List<StudentDto> getAllStudentsList() {
        return studentRepository.findAll().stream().map(this::toDto).toList();
    }

    public StudentDto getStudentById(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        return toDto(student);
    }

    public StudentDto getStudentByUserId(String userId) {
        Optional<Student> optStudent = studentRepository.findByUserId(userId);
        if (optStudent.isEmpty()) {
            // Try lookup by user's email if available
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                optStudent = studentRepository.findByEmail(user.get().getEmail());
                if (optStudent.isEmpty()) {
                    // Create a default student profile for this user
                    String roll = "STU-" + System.currentTimeMillis() % 10000;
                    Student created = Student.builder()
                            .userId(userId)
                            .rollNumber(roll)
                            .firstName(user.get().getName() != null ? user.get().getName() : "Student")
                            .lastName("")
                            .email(user.get().getEmail())
                            .department("Computer Science")
                            .year(1)
                            .status(StudentStatus.ACTIVE)
                            .build();
                    // Auto-enroll in initial course if courses exist
                    var allCourses = courseRepository.findAll();
                    if (!allCourses.isEmpty()) {
                        created.setEnrolledCourseIds(new ArrayList<>(List.of(allCourses.get(0).getId())));
                    }
                    optStudent = Optional.of(studentRepository.save(created));
                }
            }
        }
        Student student = optStudent.orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return toDto(student);
    }

    public StudentDto createStudent(StudentDto dto) {
        if (studentRepository.existsByRollNumber(dto.getRollNumber())) {
            throw new BadRequestException("Roll number already exists: " + dto.getRollNumber());
        }

        // Link or create user account
        User user = null;
        if (StringUtils.hasText(dto.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(dto.getEmail().trim().toLowerCase());
            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                String rawPassword = StringUtils.hasText(dto.getPassword()) ? dto.getPassword() : "Student@123";
                user = User.builder()
                        .name(dto.getFirstName() + " " + dto.getLastName())
                        .email(dto.getEmail().trim().toLowerCase())
                        .password(passwordEncoder.encode(rawPassword))
                        .role(Role.ROLE_STUDENT)
                        .build();
                user = userRepository.save(user);
            }
        }

        Student student = Student.builder()
                .userId(user != null ? user.getId() : dto.getUserId())
                .rollNumber(dto.getRollNumber().trim().toUpperCase())
                .firstName(dto.getFirstName().trim())
                .lastName(dto.getLastName().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .dob(dto.getDob())
                .gender(dto.getGender())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .department(dto.getDepartment().trim())
                .year(dto.getYear() != null ? dto.getYear() : 1)
                .enrolledCourseIds(dto.getEnrolledCourseIds() != null ? dto.getEnrolledCourseIds() : new ArrayList<>())
                .status(dto.getStatus() != null ? dto.getStatus() : StudentStatus.ACTIVE)
                .build();

        Student saved = studentRepository.save(student);
        return toDto(saved);
    }

    public StudentDto updateStudent(String id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        if (!student.getRollNumber().equalsIgnoreCase(dto.getRollNumber()) &&
                studentRepository.existsByRollNumber(dto.getRollNumber())) {
            throw new BadRequestException("Roll number already in use: " + dto.getRollNumber());
        }

        student.setRollNumber(dto.getRollNumber().trim().toUpperCase());
        student.setFirstName(dto.getFirstName().trim());
        student.setLastName(dto.getLastName().trim());
        student.setDob(dto.getDob());
        student.setGender(dto.getGender());
        student.setPhone(dto.getPhone());
        student.setAddress(dto.getAddress());
        student.setDepartment(dto.getDepartment().trim());
        if (dto.getYear() != null) {
            student.setYear(dto.getYear());
        }
        if (dto.getStatus() != null) {
            student.setStatus(dto.getStatus());
        }
        if (dto.getEnrolledCourseIds() != null) {
            student.setEnrolledCourseIds(dto.getEnrolledCourseIds());
        }

        // If email changed, update student and linked user
        if (StringUtils.hasText(dto.getEmail()) && !dto.getEmail().equalsIgnoreCase(student.getEmail())) {
            student.setEmail(dto.getEmail().trim().toLowerCase());
            if (student.getUserId() != null) {
                userRepository.findById(student.getUserId()).ifPresent(u -> {
                    u.setEmail(dto.getEmail().trim().toLowerCase());
                    u.setName(student.getFullName());
                    userRepository.save(u);
                });
            }
        }

        Student updated = studentRepository.save(student);
        return toDto(updated);
    }

    public void deleteStudent(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        // Remove student from any courses they were enrolled in
        if (student.getEnrolledCourseIds() != null) {
            for (String courseId : student.getEnrolledCourseIds()) {
                courseRepository.findById(courseId).ifPresent(c -> {
                    if (c.getEnrolledStudentIds() != null) {
                        c.getEnrolledStudentIds().remove(student.getId());
                        courseRepository.save(c);
                    }
                });
            }
        }

        // Optionally delete user login if it exists
        if (student.getUserId() != null) {
            userRepository.deleteById(student.getUserId());
        }

        studentRepository.delete(student);
    }

    public StudentDto toDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .userId(student.getUserId())
                .rollNumber(student.getRollNumber())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .dob(student.getDob())
                .gender(student.getGender())
                .phone(student.getPhone())
                .address(student.getAddress())
                .department(student.getDepartment())
                .year(student.getYear())
                .enrolledCourseIds(student.getEnrolledCourseIds() != null ? student.getEnrolledCourseIds() : new ArrayList<>())
                .status(student.getStatus())
                .build();
    }
}
