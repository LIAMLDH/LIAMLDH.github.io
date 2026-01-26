package org.example.student.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student")
@Data
@ToString(exclude = {"major", "selections", "account"}) // 排除引起循环引用的字段
@NoArgsConstructor @AllArgsConstructor @Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //数据库自增
    private Long id;   // 逻辑主键

    @Column(name = "student_id", unique = true, nullable = false, length = 20)
    private String studentId;  // 业务学号

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Column(unique = true, nullable = false, length = 20)
    private String phone;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_student_major"))    //major_id外键约束
    private Major major;

    @Column(name = "created_time", updatable = false, nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    /* 一对多：学生选的多门课 */
    /*mappedBy = "student" 的含义
    表示关系的被拥有方：当前实体(Student)不是关系的拥有者
    指向拥有方的属性：Student 实体通过 student 属性引用关系的拥有方(StudentCourse)
    外键由拥有方维护：外键列存在于 StudentCourse 表中，而不是 Student 表
    cascade = CascadeType.ALL 的含义
    级联所有操作：当对 Student 实体执行操作时，会自动级联到关联的 StudentCourse 实体*/
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)

    /*
        设置集合类型的默认值：在 Builder 模式中，为 List、Map 等集合类型字段提供初始默认值
        避免空指针异常：确保集合字段在对象创建时就被初始化
    */
    @Builder.Default
    private List<StudentCourse> selections = new ArrayList<>();

    /* 一对一：账号信息 */
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = true)
    private Account account;

    @PrePersist
    protected void onCreate() {
        createdTime = updatedTime = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}