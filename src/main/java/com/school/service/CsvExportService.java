package com.school.service;

import com.school.dto.GradeDto;
import com.school.dto.StudentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvExportService {

    private final StudentService studentService;
    private final GradeService gradeService;

    public byte[] exportStudentsCsv() {
        List<StudentDto> students = studentService.getAllStudentsList();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            // Header
            writer.println("Roll Number,First Name,Last Name,Email,Department,Year,Gender,Phone,Status");

            for (StudentDto s : students) {
                writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        escapeCsv(s.getRollNumber()),
                        escapeCsv(s.getFirstName()),
                        escapeCsv(s.getLastName()),
                        escapeCsv(s.getEmail()),
                        escapeCsv(s.getDepartment()),
                        s.getYear() != null ? s.getYear() : "",
                        escapeCsv(s.getGender()),
                        escapeCsv(s.getPhone()),
                        s.getStatus() != null ? s.getStatus().name() : ""
                ));
            }
        }

        return out.toByteArray();
    }

    public byte[] exportGradesCsv() {
        List<GradeDto> grades = gradeService.getAllGrades();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            // Header
            writer.println("Roll Number,Student Name,Course Code,Course Name,Exam Type,Marks Obtained,Max Marks,Percentage,Grade,GPA Points,Semester");

            for (GradeDto g : grades) {
                writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        escapeCsv(g.getStudentRollNumber()),
                        escapeCsv(g.getStudentName()),
                        escapeCsv(g.getCourseCode()),
                        escapeCsv(g.getCourseName()),
                        g.getExamType() != null ? g.getExamType().name() : "",
                        g.getMarksObtained() != null ? g.getMarksObtained() : "",
                        g.getMaxMarks() != null ? g.getMaxMarks() : "",
                        g.getPercentage() != null ? g.getPercentage() + "%" : "",
                        escapeCsv(g.getGrade()),
                        g.getGpaPoints() != null ? g.getGpaPoints() : "",
                        escapeCsv(g.getSemester())
                ));
            }
        }

        return out.toByteArray();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }
}
