package com.school.repository;

import com.school.model.Teacher;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends MongoRepository<Teacher, String> {
    Optional<Teacher> findByUserId(String userId);
    Optional<Teacher> findByEmail(String email);
    List<Teacher> findByDepartment(String department);
    Boolean existsByEmail(String email);
}
