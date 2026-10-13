package com.school.repository;

import com.school.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    Optional<Student> findByRollNumber(String rollNumber);
    Optional<Student> findByUserId(String userId);
    Optional<Student> findByEmail(String email);
    Boolean existsByRollNumber(String rollNumber);
    Boolean existsByEmail(String email);
    List<Student> findByEnrolledCourseIdsContaining(String courseId);
    List<Student> findByDepartment(String department);
    List<Student> findByYear(Integer year);

    @Query("{ $and: [" +
            "  { $or: [ " +
            "      { 'firstName': { $regex: ?0, $options: 'i' } }, " +
            "      { 'lastName': { $regex: ?0, $options: 'i' } }, " +
            "      { 'rollNumber': { $regex: ?0, $options: 'i' } }, " +
            "      { 'email': { $regex: ?0, $options: 'i' } } " +
            "  ] }," +
            "  { $expr: { $cond: [ { $eq: [?1, ''] }, true, { $eq: ['$department', ?1] } ] } }," +
            "  { $expr: { $cond: [ { $eq: [?2, null] }, true, { $eq: ['$year', ?2] } ] } }" +
            "] }")
    Page<Student> searchStudents(String query, String department, Integer year, Pageable pageable);
}
