package org.example.student.service;

import org.example.student.entity.StudentCourse;
import org.example.student.entity.Student;
import org.example.student.entity.Course;
import org.example.student.repository.StudentCourseRepository;
import org.example.student.repository.CourseRepository;
import org.example.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentCourseService {
    
    @Autowired
    private StudentCourseRepository studentCourseRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    public List<StudentCourse> getCoursesByStudentId(Long studentId) {
        return studentCourseRepository.findByStudentId(studentId);
    }
    
    @Transactional
    public boolean selectCourse(Long studentId, Long courseId) {
        // 检查学生和课程是否存在
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        
        if (studentOpt.isEmpty() || courseOpt.isEmpty()) {
            return false;
        }
        
        // 检查是否已经选过这门课
        if (studentCourseRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
            return false; // 已经选过这门课
        }
        
        // 创建选课记录
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudent(studentOpt.get());
        studentCourse.setCourse(courseOpt.get());
        
        studentCourseRepository.save(studentCourse);
        return true;
    }
    
    @Transactional
    public boolean dropCourse(Long studentId, Long courseId) {
        Optional<StudentCourse> studentCourseOpt = studentCourseRepository.findByStudentIdAndCourseId(studentId, courseId);
        
        if (studentCourseOpt.isPresent()) {
            studentCourseRepository.delete(studentCourseOpt.get());
            return true;
        }
        
        return false;
    }
    
    public Double getTotalCreditsByStudentId(Long studentId) {
        Double total = studentCourseRepository.sumCreditsByStudentId(studentId);
        return total != null ? total : 0.0;
    }
    
    public boolean existsByCourseId(Long courseId) {
        return studentCourseRepository.existsByCourseId(courseId);
    }
}