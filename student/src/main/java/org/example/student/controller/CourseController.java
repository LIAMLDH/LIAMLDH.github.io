package org.example.student.controller;

import org.example.student.dto.CourseDTO;
import org.example.student.entity.Course;
import org.example.student.service.CourseService;
import org.example.student.service.StudentCourseService;
import org.example.student.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/course")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:5176", "http://localhost:5177", "http://localhost:5178"})
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private StudentCourseService studentCourseService;
    
    // 获取所有课程
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<Result<List<CourseDTO>>> getAllCourses(HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (List<CourseDTO>) null));
        }
        
        try {
            List<Course> courses = courseService.getAllCourses();
            List<CourseDTO> courseDTOs = courses.stream()
                    .map(CourseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Result.success("获取课程列表成功", courseDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("获取课程列表失败: " + e.getMessage()));
        }
    }
    
    // 根据ID获取课程
    @RequestMapping(value = "/{id}", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<Result<CourseDTO>> getCourseById(@PathVariable Long id, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (CourseDTO) null));
        }
        
        try {
            return courseService.getCourseById(id)
                    .map(course -> ResponseEntity.ok(Result.success("获取课程成功", new CourseDTO(course))))
                    .orElseGet(() -> ResponseEntity.badRequest()
                            .body(Result.error("课程不存在")));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("获取课程失败: " + e.getMessage()));
        }
    }
    
    // 创建或更新课程
    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public ResponseEntity<Result<CourseDTO>> saveCourse(@RequestBody Course course, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (CourseDTO) null));
        }
        
        try {
            // 检查课程代码是否已存在（更新时排除自己）
            if (course.getId() == null && courseService.existsByCourseCode(course.getCourseCode())) {
                return ResponseEntity.badRequest()
                        .body(Result.error("课程代码已存在"));
            }
            
            Course savedCourse = courseService.saveCourse(course);
            CourseDTO courseDTO = new CourseDTO(savedCourse);
            return ResponseEntity.ok(Result.success("保存课程成功", courseDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("保存课程失败: " + e.getMessage()));
        }
    }
    
    // 删除课程
    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    public ResponseEntity<Result<String>> deleteCourse(@PathVariable Long id, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", "Success"));
        }
        
        try {
            // 检查是否有学生选了这门课
            if (studentCourseService.existsByCourseId(id)) {
                return ResponseEntity.badRequest()
                        .body(Result.error("有学生已选该课程，不能删除"));
            }
            
            courseService.deleteCourse(id);
            return ResponseEntity.ok(Result.success("删除课程成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("删除课程失败: " + e.getMessage()));
        }
    }
}