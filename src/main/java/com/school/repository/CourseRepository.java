package com.school.repository;

import com.school.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    Optional<Course> findByCode(String code);
    Boolean existsByCode(String code);
    List<Course> findByTeacherId(String teacherId);
    List<Course> findByDepartment(String department);
    List<Course> findBySemester(String semester);
}
