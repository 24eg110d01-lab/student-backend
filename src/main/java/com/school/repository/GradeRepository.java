package com.school.repository;

import com.school.model.ExamType;
import com.school.model.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends MongoRepository<Grade, String> {
    List<Grade> findByStudentId(String studentId);
    List<Grade> findByCourseId(String courseId);
    List<Grade> findByStudentIdAndCourseId(String studentId, String courseId);
    Optional<Grade> findByStudentIdAndCourseIdAndExamType(String studentId, String courseId, ExamType examType);
    List<Grade> findBySemester(String semester);
    List<Grade> findByStudentIdAndSemester(String studentId, String semester);
}
