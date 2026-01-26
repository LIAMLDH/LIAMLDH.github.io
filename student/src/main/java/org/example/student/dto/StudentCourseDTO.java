package org.example.student.dto;

import org.example.student.entity.StudentCourse;
import org.example.student.entity.Student;
import org.example.student.entity.Course;

import java.time.LocalDateTime;

public class StudentCourseDTO {
    private Long id;
    private AccountDTO.StudentDTO student;
    private CourseDTO course;
    private LocalDateTime selectedTime;

    public StudentCourseDTO() {}

    public StudentCourseDTO(StudentCourse studentCourse) {
        this.id = studentCourse.getId();
        this.selectedTime = studentCourse.getSelectedTime();
        
        if (studentCourse.getStudent() != null) {
            this.student = new AccountDTO.StudentDTO(studentCourse.getStudent());
        }
        
        if (studentCourse.getCourse() != null) {
            this.course = new CourseDTO(studentCourse.getCourse());
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountDTO.StudentDTO getStudent() {
        return student;
    }

    public void setStudent(AccountDTO.StudentDTO student) {
        this.student = student;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public LocalDateTime getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(LocalDateTime selectedTime) {
        this.selectedTime = selectedTime;
    }
}