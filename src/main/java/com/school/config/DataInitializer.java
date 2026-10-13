package com.school.config;

import com.school.dto.GradeDto;
import com.school.model.*;
import com.school.repository.*;
import com.school.service.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;
    private final GradeService gradeService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Database is empty. Initializing sample seed data...");
            seedData();
            log.info("Sample seed data initialization completed successfully!");
        } else {
            log.info("Database already contains users. Skipping initialization.");
        }
    }

    private void seedData() {
        // 1. Create Default Admin User
        User admin = User.builder()
                .name("System Administrator")
                .email("admin@school.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ROLE_ADMIN)
                .build();
        userRepository.save(admin);
        log.info("Admin created: admin@school.com / Admin@123");

        // 2. Create Teachers & Users
        User teacherUser1 = User.builder()
                .name("Dr. Robert Smith")
                .email("teacher@school.com")
                .password(passwordEncoder.encode("Teacher@123"))
                .role(Role.ROLE_TEACHER)
                .build();
        userRepository.save(teacherUser1);

        Teacher teacher1 = Teacher.builder()
                .userId(teacherUser1.getId())
                .name("Dr. Robert Smith")
                .email("teacher@school.com")
                .department("Computer Science")
                .phone("+1 555-0101")
                .courseIds(new ArrayList<>())
                .build();
        teacherRepository.save(teacher1);

        User teacherUser2 = User.builder()
                .name("Prof. Sarah Jenkins")
                .email("sarah.jenkins@school.com")
                .password(passwordEncoder.encode("Teacher@123"))
                .role(Role.ROLE_TEACHER)
                .build();
        userRepository.save(teacherUser2);

        Teacher teacher2 = Teacher.builder()
                .userId(teacherUser2.getId())
                .name("Prof. Sarah Jenkins")
                .email("sarah.jenkins@school.com")
                .department("Mathematics")
                .phone("+1 555-0102")
                .courseIds(new ArrayList<>())
                .build();
        teacherRepository.save(teacher2);

        User teacherUser3 = User.builder()
                .name("Dr. Michael Chang")
                .email("michael.chang@school.com")
                .password(passwordEncoder.encode("Teacher@123"))
                .role(Role.ROLE_TEACHER)
                .build();
        userRepository.save(teacherUser3);

        Teacher teacher3 = Teacher.builder()
                .userId(teacherUser3.getId())
                .name("Dr. Michael Chang")
                .email("michael.chang@school.com")
                .department("Physics")
                .phone("+1 555-0103")
                .courseIds(new ArrayList<>())
                .build();
        teacherRepository.save(teacher3);

        // 3. Create Courses
        Course course1 = Course.builder()
                .code("CS101")
                .name("Introduction to Computer Science")
                .credits(4)
                .department("Computer Science")
                .teacherId(teacher1.getId())
                .semester("Fall 2026")
                .enrolledStudentIds(new ArrayList<>())
                .build();
        courseRepository.save(course1);

        Course course2 = Course.builder()
                .code("CS201")
                .name("Data Structures & Algorithms")
                .credits(4)
                .department("Computer Science")
                .teacherId(teacher1.getId())
                .semester("Fall 2026")
                .enrolledStudentIds(new ArrayList<>())
                .build();
        courseRepository.save(course2);

        Course course3 = Course.builder()
                .code("MATH101")
                .name("Calculus & Linear Algebra")
                .credits(3)
                .department("Mathematics")
                .teacherId(teacher2.getId())
                .semester("Fall 2026")
                .enrolledStudentIds(new ArrayList<>())
                .build();
        courseRepository.save(course3);

        Course course4 = Course.builder()
                .code("PHY101")
                .name("Quantum Physics & Mechanics")
                .credits(3)
                .department("Physics")
                .teacherId(teacher3.getId())
                .semester("Fall 2026")
                .enrolledStudentIds(new ArrayList<>())
                .build();
        courseRepository.save(course4);

        // Update teachers' course lists
        teacher1.setCourseIds(Arrays.asList(course1.getId(), course2.getId()));
        teacherRepository.save(teacher1);
        teacher2.setCourseIds(Arrays.asList(course3.getId()));
        teacherRepository.save(teacher2);
        teacher3.setCourseIds(Arrays.asList(course4.getId()));
        teacherRepository.save(teacher3);

        // 4. Create Students & Users
        User studentUser1 = User.builder()
                .name("Alice Johnson")
                .email("student@school.com")
                .password(passwordEncoder.encode("Student@123"))
                .role(Role.ROLE_STUDENT)
                .build();
        userRepository.save(studentUser1);

        Student student1 = Student.builder()
                .userId(studentUser1.getId())
                .rollNumber("CS2026-001")
                .firstName("Alice")
                .lastName("Johnson")
                .email("student@school.com")
                .dob(LocalDate.of(2004, 5, 14))
                .gender("Female")
                .phone("+1 555-0201")
                .address("123 Campus Way, Apt 4B")
                .department("Computer Science")
                .year(2)
                .status(StudentStatus.ACTIVE)
                .enrolledCourseIds(new ArrayList<>(Arrays.asList(course1.getId(), course2.getId(), course3.getId())))
                .build();
        studentRepository.save(student1);

        User studentUser2 = User.builder()
                .name("Bob Miller")
                .email("bob.miller@school.com")
                .password(passwordEncoder.encode("Student@123"))
                .role(Role.ROLE_STUDENT)
                .build();
        userRepository.save(studentUser2);

        Student student2 = Student.builder()
                .userId(studentUser2.getId())
                .rollNumber("CS2026-002")
                .firstName("Bob")
                .lastName("Miller")
                .email("bob.miller@school.com")
                .dob(LocalDate.of(2003, 9, 21))
                .gender("Male")
                .phone("+1 555-0202")
                .address("456 University Ave")
                .department("Computer Science")
                .year(2)
                .status(StudentStatus.ACTIVE)
                .enrolledCourseIds(new ArrayList<>(Arrays.asList(course1.getId(), course3.getId())))
                .build();
        studentRepository.save(student2);

        User studentUser3 = User.builder()
                .name("Charlie Brown")
                .email("charlie.brown@school.com")
                .password(passwordEncoder.encode("Student@123"))
                .role(Role.ROLE_STUDENT)
                .build();
        userRepository.save(studentUser3);

        Student student3 = Student.builder()
                .userId(studentUser3.getId())
                .rollNumber("MATH2026-003")
                .firstName("Charlie")
                .lastName("Brown")
                .email("charlie.brown@school.com")
                .dob(LocalDate.of(2005, 1, 10))
                .gender("Male")
                .phone("+1 555-0203")
                .address("789 College Blvd")
                .department("Mathematics")
                .year(1)
                .status(StudentStatus.ACTIVE)
                .enrolledCourseIds(new ArrayList<>(Arrays.asList(course3.getId(), course4.getId())))
                .build();
        studentRepository.save(student3);

        User studentUser4 = User.builder()
                .name("Diana Prince")
                .email("diana.prince@school.com")
                .password(passwordEncoder.encode("Student@123"))
                .role(Role.ROLE_STUDENT)
                .build();
        userRepository.save(studentUser4);

        Student student4 = Student.builder()
                .userId(studentUser4.getId())
                .rollNumber("PHY2026-004")
                .firstName("Diana")
                .lastName("Prince")
                .email("diana.prince@school.com")
                .dob(LocalDate.of(2002, 11, 28))
                .gender("Female")
                .phone("+1 555-0204")
                .address("321 Science Park")
                .department("Physics")
                .year(3)
                .status(StudentStatus.ACTIVE)
                .enrolledCourseIds(new ArrayList<>(Arrays.asList(course4.getId())))
                .build();
        studentRepository.save(student4);

        // Update Course Enrolled Students
        course1.setEnrolledStudentIds(Arrays.asList(student1.getId(), student2.getId()));
        courseRepository.save(course1);
        course2.setEnrolledStudentIds(Arrays.asList(student1.getId()));
        courseRepository.save(course2);
        course3.setEnrolledStudentIds(Arrays.asList(student1.getId(), student2.getId(), student3.getId()));
        courseRepository.save(course3);
        course4.setEnrolledStudentIds(Arrays.asList(student3.getId(), student4.getId()));
        courseRepository.save(course4);

        // 5. Seed Attendance for Past 5 Days
        LocalDate today = LocalDate.now();
        for (int i = 5; i >= 1; i--) {
            LocalDate date = today.minusDays(i);

            // CS101 attendance
            attendanceRepository.save(Attendance.builder()
                    .studentId(student1.getId())
                    .courseId(course1.getId())
                    .date(date)
                    .status(i == 2 ? AttendanceStatus.LATE : AttendanceStatus.PRESENT)
                    .remarks(i == 2 ? "Traffic delay" : "On time")
                    .build());

            attendanceRepository.save(Attendance.builder()
                    .studentId(student2.getId())
                    .courseId(course1.getId())
                    .date(date)
                    .status(i == 4 ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT)
                    .remarks(i == 4 ? "Sick leave" : "Present")
                    .build());

            // MATH101 attendance
            attendanceRepository.save(Attendance.builder()
                    .studentId(student1.getId())
                    .courseId(course3.getId())
                    .date(date)
                    .status(AttendanceStatus.PRESENT)
                    .remarks("Attentive in class")
                    .build());

            attendanceRepository.save(Attendance.builder()
                    .studentId(student3.getId())
                    .courseId(course3.getId())
                    .date(date)
                    .status(i == 1 ? AttendanceStatus.LATE : AttendanceStatus.PRESENT)
                    .remarks("Normal")
                    .build());
        }

        // 6. Seed Grades
        gradeService.saveOrUpdateGrade(GradeDto.builder()
                .studentId(student1.getId())
                .courseId(course1.getId())
                .examType(ExamType.MIDTERM)
                .marksObtained(92.0)
                .maxMarks(100.0)
                .semester("Fall 2026")
                .remarks("Excellent algorithm comprehension")
                .build());

        gradeService.saveOrUpdateGrade(GradeDto.builder()
                .studentId(student1.getId())
                .courseId(course2.getId())
                .examType(ExamType.MIDTERM)
                .marksObtained(88.0)
                .maxMarks(100.0)
                .semester("Fall 2026")
                .remarks("Solid code quality in assignments")
                .build());

        gradeService.saveOrUpdateGrade(GradeDto.builder()
                .studentId(student1.getId())
                .courseId(course3.getId())
                .examType(ExamType.MIDTERM)
                .marksObtained(95.0)
                .maxMarks(100.0)
                .semester("Fall 2026")
                .remarks("Outstanding calculus proof")
                .build());

        gradeService.saveOrUpdateGrade(GradeDto.builder()
                .studentId(student2.getId())
                .courseId(course1.getId())
                .examType(ExamType.MIDTERM)
                .marksObtained(74.0)
                .maxMarks(100.0)
                .semester("Fall 2026")
                .remarks("Good effort, review tree structures")
                .build());

        gradeService.saveOrUpdateGrade(GradeDto.builder()
                .studentId(student3.getId())
                .courseId(course3.getId())
                .examType(ExamType.MIDTERM)
                .marksObtained(81.0)
                .maxMarks(100.0)
                .semester("Fall 2026")
                .remarks("Well structured calculations")
                .build());
    }
}
