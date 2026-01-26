package org.example.student.controller;

import org.example.student.dto.StudentCourseDTO;
import org.example.student.entity.StudentCourse;
import org.example.student.entity.Student;
import org.example.student.entity.Course;
import org.example.student.repository.StudentCourseRepository;
import org.example.student.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enrollment")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:5176", "http://localhost:5177", "http://localhost:5178"})
public class EnrollmentController {
    
    @Autowired
    private StudentCourseRepository studentCourseRepository;
    
    // 获取所有选课信息
    @RequestMapping(value = "/all", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<Result<List<StudentCourseDTO>>> getAllEnrollments(HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (List<StudentCourseDTO>) null));
        }
        
        try {
            List<StudentCourse> enrollments = studentCourseRepository.findAll();
            List<StudentCourseDTO> enrollmentDTOs = enrollments.stream()
                    .map(StudentCourseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Result.success("获取选课信息成功", enrollmentDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("获取选课信息失败: " + e.getMessage()));
        }
    }
    
    // 根据学生ID获取选课信息
    @RequestMapping(value = "/student/{studentId}", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<Result<List<StudentCourseDTO>>> getEnrollmentsByStudentId(@PathVariable Long studentId, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (List<StudentCourseDTO>) null));
        }
        
        try {
            List<StudentCourse> enrollments = studentCourseRepository.findByStudentId(studentId);
            List<StudentCourseDTO> enrollmentDTOs = enrollments.stream()
                    .map(StudentCourseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Result.success("获取学生选课信息成功", enrollmentDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("获取学生选课信息失败: " + e.getMessage()));
        }
    }
    
    // 根据课程ID获取选课信息
    @RequestMapping(value = "/course/{courseId}", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<Result<List<StudentCourseDTO>>> getEnrollmentsByCourseId(@PathVariable Long courseId, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (List<StudentCourseDTO>) null));
        }
        
        try {
            List<StudentCourse> enrollments = studentCourseRepository.findAll()
                    .stream()
                    .filter(enrollment -> enrollment.getCourse().getId().equals(courseId))
                    .collect(Collectors.toList());
            List<StudentCourseDTO> enrollmentDTOs = enrollments.stream()
                    .map(StudentCourseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Result.success("获取课程选课信息成功", enrollmentDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("获取课程选课信息失败: " + e.getMessage()));
        }
    }
    
    // 获取选课统计信息
    @RequestMapping(value = "/statistics", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<Result<Map<String, Object>>> getEnrollmentStatistics(HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (Map<String, Object>) null));
        }
        
        try {
            List<StudentCourse> enrollments = studentCourseRepository.findAll();
            
            // 统计信息
            long totalEnrollments = enrollments.size();
            long uniqueStudents = enrollments.stream()
                    .map(enrollment -> enrollment.getStudent().getId())
                    .distinct()
                    .count();
            long uniqueCourses = enrollments.stream()
                    .map(enrollment -> enrollment.getCourse().getId())
                    .distinct()
                    .count();
            
            Map<String, Object> statistics = new java.util.HashMap<>();
            statistics.put("totalEnrollments", totalEnrollments);
            statistics.put("uniqueStudents", uniqueStudents);
            statistics.put("uniqueCourses", uniqueCourses);
            
            return ResponseEntity.ok(Result.success("获取统计信息成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("获取统计信息失败: " + e.getMessage()));
        }
    }
}