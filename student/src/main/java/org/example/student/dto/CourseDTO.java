package org.example.student.dto;

import org.example.student.entity.Course;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CourseDTO {
    private Long id;
    private String courseCode;
    private String courseName;
    private String description;
    private BigDecimal credits;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public CourseDTO() {}

    public CourseDTO(Course course) {
        this.id = course.getId();
        this.courseCode = course.getCourseCode();
        this.courseName = course.getCourseName();
        this.description = course.getDescription();
        this.credits = course.getCredits();
        this.createdTime = course.getCreatedTime();
        this.updatedTime = course.getUpdatedTime();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}