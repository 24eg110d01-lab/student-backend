package com.school.service;

import com.school.dto.GradeDto;
import com.school.exception.BadRequestException;
import com.school.model.ExamType;
import com.school.model.Grade;
import com.school.repository.CourseRepository;
import com.school.repository.GradeRepository;
import com.school.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private GradeService gradeService;

    @Test
    void computeLetterGrade_CorrectRanges() {
        assertEquals("A+", GradeService.computeLetterGrade(95.0));
        assertEquals("A", GradeService.computeLetterGrade(85.0));
        assertEquals("B+", GradeService.computeLetterGrade(78.0));
        assertEquals("B", GradeService.computeLetterGrade(72.0));
        assertEquals("C+", GradeService.computeLetterGrade(67.0));
        assertEquals("C", GradeService.computeLetterGrade(62.0));
        assertEquals("D+", GradeService.computeLetterGrade(57.0));
        assertEquals("D", GradeService.computeLetterGrade(51.0));
        assertEquals("F", GradeService.computeLetterGrade(45.0));
    }

    @Test
    void computeGpaPoints_CorrectValues() {
        assertEquals(4.0, GradeService.computeGpaPoints(92.0));
        assertEquals(3.7, GradeService.computeGpaPoints(82.0));
        assertEquals(3.0, GradeService.computeGpaPoints(72.0));
        assertEquals(2.0, GradeService.computeGpaPoints(62.0), 0.5);
        assertEquals(0.0, GradeService.computeGpaPoints(40.0));
    }

    @Test
    void saveOrUpdateGrade_MarksExceedMax_ThrowsBadRequest() {
        GradeDto dto = GradeDto.builder()
                .studentId("std-1")
                .courseId("crs-1")
                .examType(ExamType.MIDTERM)
                .marksObtained(105.0)
                .maxMarks(100.0)
                .build();

        assertThrows(BadRequestException.class, () -> gradeService.saveOrUpdateGrade(dto));
    }

    @Test
    void saveOrUpdateGrade_Success() {
        GradeDto dto = GradeDto.builder()
                .studentId("std-1")
                .courseId("crs-1")
                .examType(ExamType.MIDTERM)
                .marksObtained(85.0)
                .maxMarks(100.0)
                .semester("Fall 2026")
                .build();

        when(gradeRepository.findByStudentIdAndCourseIdAndExamType("std-1", "crs-1", ExamType.MIDTERM))
                .thenReturn(Optional.empty());

        when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> {
            Grade g = invocation.getArgument(0);
            g.setId("grd-1");
            return g;
        });

        GradeDto result = gradeService.saveOrUpdateGrade(dto);

        assertNotNull(result);
        assertEquals("A", result.getGrade());
        assertEquals(3.7, result.getGpaPoints());
        assertEquals(85.0, result.getPercentage());
    }
}
