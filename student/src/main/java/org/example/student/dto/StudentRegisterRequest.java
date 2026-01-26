package org.example.student.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentRegisterRequest {
    private String name;
    private Integer age;
    private String phone;
    private LocalDate enrollmentDate;
    private Long majorId;
}