package org.example.student.dto;

import org.example.student.entity.Account;
import org.example.student.entity.Student;
import org.example.student.entity.Major;

import java.time.LocalDateTime;

public class AccountDTO {
    private Long id;
    private String username;
    private String password;
    private Account.Role role;
    private Boolean firstLogin;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private StudentDTO student;

    public AccountDTO() {}

    public AccountDTO(Account account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.password = account.getPassword();
        this.role = account.getRole();
        this.firstLogin = account.getFirstLogin();
        this.createdTime = account.getCreatedTime();
        this.updatedTime = account.getUpdatedTime();
        
        if (account.getStudent() != null) {
            this.student = new StudentDTO(account.getStudent());
        }
    }

    // 静态工厂方法，用于从Account实体创建AccountDTO
    public static AccountDTO from(Account account) {
        return new AccountDTO(account);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Account.Role getRole() {
        return role;
    }

    public void setRole(Account.Role role) {
        this.role = role;
    }

    public Boolean getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
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

    public StudentDTO getStudent() {
        return student;
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
    }

    // Student DTO class
    public static class StudentDTO {
        private Long id;
        private String studentId;
        private String name;
        private Integer age;
        private String phone;
        private java.time.LocalDate enrollmentDate;
        private Integer sequenceNumber;
        private MajorDTO major;

        public StudentDTO() {}

        public StudentDTO(Student student) {
            this.id = student.getId();
            this.studentId = student.getStudentId();
            this.name = student.getName();
            this.age = student.getAge();
            this.phone = student.getPhone();
            this.enrollmentDate = student.getEnrollmentDate();
            this.sequenceNumber = student.getSequenceNumber();
            
            if (student.getMajor() != null) {
                this.major = new MajorDTO(student.getMajor());
            }
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public java.time.LocalDate getEnrollmentDate() {
            return enrollmentDate;
        }

        public void setEnrollmentDate(java.time.LocalDate enrollmentDate) {
            this.enrollmentDate = enrollmentDate;
        }

        public Integer getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(Integer sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        public MajorDTO getMajor() {
            return major;
        }

        public void setMajor(MajorDTO major) {
            this.major = major;
        }
    }

    // Major DTO class
    public static class MajorDTO {
        private Long id;
        private String majorCode;
        private String majorName;
        private String description;

        public MajorDTO() {}

        public MajorDTO(Major major) {
            this.id = major.getId();
            this.majorCode = major.getMajorCode();
            this.majorName = major.getMajorName();
            this.description = major.getDescription();
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
    }
}