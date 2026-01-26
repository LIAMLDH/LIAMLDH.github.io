package org.example.student.controller;

import org.example.student.dto.StudentCourseDTO;
import org.example.student.entity.StudentCourse;
import org.example.student.entity.Course;
import org.example.student.service.StudentCourseService;
import org.example.student.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student-course")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:5176", "http://localhost:5177", "http://localhost:5178"})
public class StudentCourseController {
    
    @Autowired
    private StudentCourseService studentCourseService;
    
    // 获取学生已选课程
    @RequestMapping(value = "/selected/{studentId}", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<Result<List<StudentCourseDTO>>> getSelectedCourses(@PathVariable Long studentId, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (List<StudentCourseDTO>) null));
        }
        
        try {
            List<StudentCourse> courses = studentCourseService.getCoursesByStudentId(studentId);
            List<StudentCourseDTO> courseDTOs = courses.stream()
                    .map(StudentCourseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Result.success("获取已选课程成功", courseDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("获取已选课程失败: " + e.getMessage()));
        }
    }
    
    // 选课
    @RequestMapping(value = "/select", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public ResponseEntity<Result<String>> selectCourse(@RequestParam Long studentId, @RequestParam Long courseId, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", "Success"));
        }
        
        try {
            boolean success = studentCourseService.selectCourse(studentId, courseId);
            if (success) {
                return ResponseEntity.ok(Result.success("选课成功"));
            } else {
                return ResponseEntity.badRequest()
                        .body(Result.error("选课失败，可能已选过该课程或数据不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("选课失败: " + e.getMessage()));
        }
    }
    
    // 退课
    @RequestMapping(value = "/drop", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    public ResponseEntity<Result<String>> dropCourse(@RequestParam Long studentId, @RequestParam Long courseId, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", "Success"));
        }
        
        try {
            boolean success = studentCourseService.dropCourse(studentId, courseId);
            if (success) {
                return ResponseEntity.ok(Result.success("退课成功"));
            } else {
                return ResponseEntity.badRequest()
                        .body(Result.error("退课失败，可能未选过该课程"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("退课失败: " + e.getMessage()));
        }
    }
    
    // 获取学生已选课程总学分
    @RequestMapping(value = "/credits/{studentId}", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<Result<Double>> getTotalCredits(@PathVariable Long studentId, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (Double) null));
        }
        
        try {
            Double totalCredits = studentCourseService.getTotalCreditsByStudentId(studentId);
            return ResponseEntity.ok(Result.success("获取总学分成功", totalCredits));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("获取总学分失败: " + e.getMessage()));
        }
    }
}