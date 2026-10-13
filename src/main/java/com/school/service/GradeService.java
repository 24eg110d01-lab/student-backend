package com.school.service;

import com.school.dto.GradeDto;
import com.school.dto.StudentDto;
import com.school.dto.StudentReportDto;
import com.school.exception.BadRequestException;
import com.school.exception.ResourceNotFoundException;
import com.school.model.Course;
import com.school.model.Grade;
import com.school.model.Student;
import com.school.repository.CourseRepository;
import com.school.repository.GradeRepository;
import com.school.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentService studentService;
    private final AttendanceService attendanceService;

    public GradeDto saveOrUpdateGrade(GradeDto dto) {
        if (dto.getMarksObtained() > dto.getMaxMarks()) {
            throw new BadRequestException("Marks obtained (" + dto.getMarksObtained() + 
                    ") cannot exceed max marks (" + dto.getMaxMarks() + ")");
        }

        double percentage = Math.round((dto.getMarksObtained() / dto.getMaxMarks()) * 1000.0) / 10.0;
        String letterGrade = computeLetterGrade(percentage);
        double gpaPoints = computeGpaPoints(percentage);

        Grade grade;
        if (dto.getId() != null) {
            grade = gradeRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Grade not found with ID: " + dto.getId()));
            grade.setMarksObtained(dto.getMarksObtained());
            grade.setMaxMarks(dto.getMaxMarks());
            grade.setPercentage(percentage);
            grade.setGrade(letterGrade);
            grade.setGpaPoints(gpaPoints);
            grade.setExamType(dto.getExamType());
            grade.setSemester(dto.getSemester());
            grade.setRemarks(dto.getRemarks());
        } else {
            // Check if already exists for this student, course, examType
            Optional<Grade> existing = gradeRepository.findByStudentIdAndCourseIdAndExamType(
                    dto.getStudentId(), dto.getCourseId(), dto.getExamType()
            );

            if (existing.isPresent()) {
                grade = existing.get();
                grade.setMarksObtained(dto.getMarksObtained());
                grade.setMaxMarks(dto.getMaxMarks());
                grade.setPercentage(percentage);
                grade.setGrade(letterGrade);
                grade.setGpaPoints(gpaPoints);
                grade.setSemester(dto.getSemester());
                grade.setRemarks(dto.getRemarks());
            } else {
                grade = Grade.builder()
                        .studentId(dto.getStudentId())
                        .courseId(dto.getCourseId())
                        .examType(dto.getExamType())
                        .marksObtained(dto.getMarksObtained())
                        .maxMarks(dto.getMaxMarks())
                        .percentage(percentage)
                        .grade(letterGrade)
                        .gpaPoints(gpaPoints)
                        .semester(dto.getSemester())
                        .remarks(dto.getRemarks())
                        .build();
            }
        }

        Grade saved = gradeRepository.save(grade);
        return toDto(saved);
    }

    public List<GradeDto> getGradesByStudent(String studentId) {
        return gradeRepository.findByStudentId(studentId).stream()
                .map(this::toDto)
                .toList();
    }

    public List<GradeDto> getGradesByCourse(String courseId) {
        return gradeRepository.findByCourseId(courseId).stream()
                .map(this::toDto)
                .toList();
    }

    public List<GradeDto> getAllGrades() {
        return gradeRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public void deleteGrade(String id) {
        if (!gradeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Grade not found with ID: " + id);
        }
        gradeRepository.deleteById(id);
    }

    public StudentReportDto getStudentReport(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        List<GradeDto> gradeDtos = grades.stream().map(this::toDto).toList();

        // Calculate GPA weighted by course credits
        double totalWeightedPoints = 0.0;
        int totalCredits = 0;

        for (Grade g : grades) {
            int credits = 3; // Default 3 credits
            Optional<Course> c = courseRepository.findById(g.getCourseId());
            if (c.isPresent() && c.get().getCredits() != null) {
                credits = c.get().getCredits();
            }
            totalWeightedPoints += (g.getGpaPoints() * credits);
            totalCredits += credits;
        }

        double gpa = totalCredits > 0 
                ? Math.round((totalWeightedPoints / totalCredits) * 100.0) / 100.0 
                : 0.0;

        var attendanceSummary = attendanceService.getAttendanceSummaryByStudent(studentId);

        return StudentReportDto.builder()
                .student(studentService.toDto(student))
                .grades(gradeDtos)
                .gpa(gpa)
                .totalCredits(totalCredits)
                .attendancePercentage(attendanceSummary.getAttendancePercentage())
                .attendanceSummary(attendanceSummary)
                .build();
    }

    public static String computeLetterGrade(double percentage) {
        if (percentage >= 90.0) return "A+";
        if (percentage >= 80.0) return "A";
        if (percentage >= 75.0) return "B+";
        if (percentage >= 70.0) return "B";
        if (percentage >= 65.0) return "C+";
        if (percentage >= 60.0) return "C";
        if (percentage >= 55.0) return "D+";
        if (percentage >= 50.0) return "D";
        return "F";
    }

    public static double computeGpaPoints(double percentage) {
        if (percentage >= 90.0) return 4.0;
        if (percentage >= 80.0) return 3.7;
        if (percentage >= 75.0) return 3.3;
        if (percentage >= 70.0) return 3.0;
        if (percentage >= 65.0) return 2.7;
        if (percentage >= 60.0) return 2.3;
        if (percentage >= 55.0) return 1.7;
        if (percentage >= 50.0) return 1.0;
        return 0.0;
    }

    public GradeDto toDto(Grade grade) {
        String studentName = "Unknown";
        String rollNumber = "";
        Optional<Student> student = studentRepository.findById(grade.getStudentId());
        if (student.isPresent()) {
            studentName = student.get().getFullName();
            rollNumber = student.get().getRollNumber();
        }

        String courseName = "Unknown";
        String courseCode = "";
        Optional<Course> course = courseRepository.findById(grade.getCourseId());
        if (course.isPresent()) {
            courseName = course.get().getName();
            courseCode = course.get().getCode();
        }

        return GradeDto.builder()
                .id(grade.getId())
                .studentId(grade.getStudentId())
                .studentRollNumber(rollNumber)
                .studentName(studentName)
                .courseId(grade.getCourseId())
                .courseCode(courseCode)
                .courseName(courseName)
                .examType(grade.getExamType())
                .marksObtained(grade.getMarksObtained())
                .maxMarks(grade.getMaxMarks())
                .percentage(grade.getPercentage())
                .grade(grade.getGrade())
                .gpaPoints(grade.getGpaPoints())
                .semester(grade.getSemester())
                .remarks(grade.getRemarks())
                .build();
    }
}
