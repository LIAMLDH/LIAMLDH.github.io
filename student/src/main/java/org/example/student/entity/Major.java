package org.example.student.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "major")
@Data
@ToString(exclude = "students") // 排除引起循环引用的字段
@NoArgsConstructor @AllArgsConstructor @Builder
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "major_code", unique = true, nullable = false, length = 20)
    private String majorCode;

    @Column(name = "major_name", nullable = false, length = 100)
    private String majorName;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "created_time", updatable = false, nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    /* 一对多：一个专业包含多个学生 */
    @OneToMany(mappedBy = "major", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Student> students = new ArrayList<>();

    /* 时间戳自动维护 */
    @PrePersist
    protected void onCreate() {
        createdTime = updatedTime = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}