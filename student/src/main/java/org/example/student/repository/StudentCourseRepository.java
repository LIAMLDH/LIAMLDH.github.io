package org.example.student.repository;

import org.example.student.entity.StudentCourse;
import org.example.student.entity.Student;
import org.example.student.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    List<StudentCourse> findByStudentId(Long studentId);
    
    Optional<StudentCourse> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    @Query("SELECT SUM(sc.course.credits) FROM StudentCourse sc WHERE sc.student.id = :studentId")
    Double sumCreditsByStudentId(Long studentId);
    
    boolean existsByCourseId(Long courseId);
}