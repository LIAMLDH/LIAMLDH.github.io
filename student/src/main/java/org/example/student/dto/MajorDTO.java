package org.example.student.dto;

import org.example.student.entity.Major;

import java.time.LocalDateTime;

public class MajorDTO {
    private Long id;
    private String majorCode;
    private String majorName;
    private String description;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public MajorDTO() {}

    public MajorDTO(Major major) {
        this.id = major.getId();
        this.majorCode = major.getMajorCode();
        this.majorName = major.getMajorName();
        this.description = major.getDescription();
        this.createdTime = major.getCreatedTime();
        this.updatedTime = major.getUpdatedTime();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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