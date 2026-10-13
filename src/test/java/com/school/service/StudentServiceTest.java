package com.school.service;

import com.school.dto.StudentDto;
import com.school.exception.BadRequestException;
import com.school.exception.ResourceNotFoundException;
import com.school.model.Student;
import com.school.model.StudentStatus;
import com.school.repository.CourseRepository;
import com.school.repository.StudentRepository;
import com.school.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private StudentDto studentDto;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id("std-1")
                .rollNumber("CS001")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@school.com")
                .department("Computer Science")
                .year(1)
                .status(StudentStatus.ACTIVE)
                .build();

        studentDto = StudentDto.builder()
                .rollNumber("CS001")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@school.com")
                .department("Computer Science")
                .year(1)
                .status(StudentStatus.ACTIVE)
                .build();
    }

    @Test
    void getStudentById_Success() {
        when(studentRepository.findById("std-1")).thenReturn(Optional.of(student));

        StudentDto result = studentService.getStudentById("std-1");

        assertNotNull(result);
        assertEquals("CS001", result.getRollNumber());
        assertEquals("John", result.getFirstName());
    }

    @Test
    void getStudentById_NotFound_ThrowsException() {
        when(studentRepository.findById("std-999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.getStudentById("std-999"));
    }

    @Test
    void createStudent_RollNumberExists_ThrowsBadRequest() {
        when(studentRepository.existsByRollNumber("CS001")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> studentService.createStudent(studentDto));
    }

    @Test
    void createStudent_Success() {
        when(studentRepository.existsByRollNumber("CS001")).thenReturn(false);
        when(userRepository.findByEmail("john.doe@school.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed-pwd");
        when(userRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(studentRepository.save(any())).thenReturn(student);

        StudentDto result = studentService.createStudent(studentDto);

        assertNotNull(result);
        assertEquals("CS001", result.getRollNumber());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void deleteStudent_Success() {
        when(studentRepository.findById("std-1")).thenReturn(Optional.of(student));

        studentService.deleteStudent("std-1");

        verify(studentRepository, times(1)).delete(student);
    }
}
