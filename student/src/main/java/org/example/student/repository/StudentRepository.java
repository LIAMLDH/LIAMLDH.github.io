package org.example.student.repository;

import org.example.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByPhone(String phone);
    Optional<Student> findByStudentId(String studentId);
}