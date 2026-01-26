package org.example.student.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_course",
        uniqueConstraints = @UniqueConstraint(name = "uk_student_course",
                columnNames = {"student_id", "course_id"}))
@Data
@ToString(exclude = {"student", "course"}) // 排除引起循环引用的字段
@NoArgsConstructor @AllArgsConstructor @Builder
public class StudentCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //数据库自增
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sc_student"))
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sc_course"))
    private Course course;

    @Column(name = "selected_time", nullable = false, updatable = false)
    private LocalDateTime selectedTime;

    @PrePersist
    protected void onCreate() {
        selectedTime = LocalDateTime.now();
    }
}