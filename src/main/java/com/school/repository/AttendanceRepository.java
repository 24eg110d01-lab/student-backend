package com.school.repository;

import com.school.model.Attendance;
import com.school.model.AttendanceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends MongoRepository<Attendance, String> {
    List<Attendance> findByStudentId(String studentId);
    List<Attendance> findByCourseId(String courseId);
    List<Attendance> findByCourseIdAndDate(String courseId, LocalDate date);
    Optional<Attendance> findByStudentIdAndCourseIdAndDate(String studentId, String courseId, LocalDate date);
    List<Attendance> findByStudentIdAndCourseId(String studentId, String courseId);
    long countByStudentIdAndStatus(String studentId, AttendanceStatus status);
    long countByStudentId(String studentId);
    long countByStatus(AttendanceStatus status);
}
