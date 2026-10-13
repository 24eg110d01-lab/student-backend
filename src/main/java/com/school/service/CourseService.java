package com.school.service;

import com.school.dto.CourseDto;
import com.school.dto.StudentDto;
import com.school.exception.BadRequestException;
import com.school.exception.ResourceNotFoundException;
import com.school.model.Course;
import com.school.model.Student;
import com.school.model.Teacher;
import com.school.repository.CourseRepository;
import com.school.repository.StudentRepository;
import com.school.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream().map(this::toDto).toList();
    }

    public CourseDto getCourseById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
        return toDto(course);
    }

    public List<CourseDto> getCoursesByTeacher(String teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream().map(this::toDto).toList();
    }

    public List<CourseDto> getCoursesByStudent(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        if (student.getEnrolledCourseIds() == null || student.getEnrolledCourseIds().isEmpty()) {
            return new ArrayList<>();
        }
        return courseRepository.findAllById(student.getEnrolledCourseIds()).stream().map(this::toDto).toList();
    }

    public CourseDto createCourse(CourseDto dto) {
        if (courseRepository.existsByCode(dto.getCode().trim().toUpperCase())) {
            throw new BadRequestException("Course code already exists: " + dto.getCode());
        }

        Course course = Course.builder()
                .code(dto.getCode().trim().toUpperCase())
                .name(dto.getName().trim())
                .credits(dto.getCredits())
                .department(dto.getDepartment().trim())
                .teacherId(dto.getTeacherId())
                .semester(dto.getSemester().trim())
                .enrolledStudentIds(new ArrayList<>())
                .build();

        Course saved = courseRepository.save(course);

        // Update teacher's assigned courses
        if (dto.getTeacherId() != null) {
            teacherRepository.findById(dto.getTeacherId()).ifPresent(teacher -> {
                if (teacher.getCourseIds() == null) {
                    teacher.setCourseIds(new ArrayList<>());
                }
                if (!teacher.getCourseIds().contains(saved.getId())) {
                    teacher.getCourseIds().add(saved.getId());
                    teacherRepository.save(teacher);
                }
            });
        }

        return toDto(saved);
    }

    public CourseDto updateCourse(String id, CourseDto dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));

        if (!course.getCode().equalsIgnoreCase(dto.getCode()) &&
                courseRepository.existsByCode(dto.getCode().trim().toUpperCase())) {
            throw new BadRequestException("Course code already exists: " + dto.getCode());
        }

        String oldTeacherId = course.getTeacherId();

        course.setCode(dto.getCode().trim().toUpperCase());
        course.setName(dto.getName().trim());
        course.setCredits(dto.getCredits());
        course.setDepartment(dto.getDepartment().trim());
        course.setTeacherId(dto.getTeacherId());
        course.setSemester(dto.getSemester().trim());

        Course updated = courseRepository.save(course);

        // Update teacher assignments if changed
        if (oldTeacherId != null && !oldTeacherId.equals(dto.getTeacherId())) {
            teacherRepository.findById(oldTeacherId).ifPresent(t -> {
                if (t.getCourseIds() != null) {
                    t.getCourseIds().remove(id);
                    teacherRepository.save(t);
                }
            });
        }
        if (dto.getTeacherId() != null) {
            teacherRepository.findById(dto.getTeacherId()).ifPresent(t -> {
                if (t.getCourseIds() == null) {
                    t.setCourseIds(new ArrayList<>());
                }
                if (!t.getCourseIds().contains(id)) {
                    t.getCourseIds().add(id);
                    teacherRepository.save(t);
                }
            });
        }

        return toDto(updated);
    }

    public void deleteCourse(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));

        // Remove from teachers
        if (course.getTeacherId() != null) {
            teacherRepository.findById(course.getTeacherId()).ifPresent(t -> {
                if (t.getCourseIds() != null) {
                    t.getCourseIds().remove(id);
                    teacherRepository.save(t);
                }
            });
        }

        // Remove from enrolled students
        List<Student> students = studentRepository.findByEnrolledCourseIdsContaining(id);
        for (Student student : students) {
            if (student.getEnrolledCourseIds() != null) {
                student.getEnrolledCourseIds().remove(id);
                studentRepository.save(student);
            }
        }

        courseRepository.delete(course);
    }

    public CourseDto enrollStudents(String courseId, List<String> studentIds) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        if (course.getEnrolledStudentIds() == null) {
            course.setEnrolledStudentIds(new ArrayList<>());
        }

        for (String studentId : studentIds) {
            Optional<Student> optStudent = studentRepository.findById(studentId);
            if (optStudent.isPresent()) {
                Student student = optStudent.get();
                if (!course.getEnrolledStudentIds().contains(studentId)) {
                    course.getEnrolledStudentIds().add(studentId);
                }
                if (student.getEnrolledCourseIds() == null) {
                    student.setEnrolledCourseIds(new ArrayList<>());
                }
                if (!student.getEnrolledCourseIds().contains(courseId)) {
                    student.getEnrolledCourseIds().add(courseId);
                    studentRepository.save(student);
                }
            }
        }

        Course updated = courseRepository.save(course);
        return toDto(updated);
    }

    public CourseDto dropStudent(String courseId, String studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        if (course.getEnrolledStudentIds() != null) {
            course.getEnrolledStudentIds().remove(studentId);
        }

        studentRepository.findById(studentId).ifPresent(student -> {
            if (student.getEnrolledCourseIds() != null) {
                student.getEnrolledCourseIds().remove(courseId);
                studentRepository.save(student);
            }
        });

        Course updated = courseRepository.save(course);
        return toDto(updated);
    }

    public List<StudentDto> getEnrolledStudents(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        if (course.getEnrolledStudentIds() == null || course.getEnrolledStudentIds().isEmpty()) {
            return new ArrayList<>();
        }

        return studentRepository.findAllById(course.getEnrolledStudentIds()).stream()
                .map(studentService::toDto)
                .toList();
    }

    public CourseDto toDto(Course course) {
        String teacherName = null;
        if (course.getTeacherId() != null) {
            teacherName = teacherRepository.findById(course.getTeacherId())
                    .map(Teacher::getName)
                    .orElse("Unassigned");
        }

        int enrolledCount = course.getEnrolledStudentIds() != null ? course.getEnrolledStudentIds().size() : 0;

        return CourseDto.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .credits(course.getCredits())
                .department(course.getDepartment())
                .teacherId(course.getTeacherId())
                .teacherName(teacherName)
                .semester(course.getSemester())
                .enrolledStudentIds(course.getEnrolledStudentIds() != null ? course.getEnrolledStudentIds() : new ArrayList<>())
                .enrolledCount(enrolledCount)
                .build();
    }
}
