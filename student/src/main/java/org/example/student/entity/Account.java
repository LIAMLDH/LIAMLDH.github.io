package org.example.student.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Data
@ToString(exclude = "student") // 排除引起循环引用的字段
@NoArgsConstructor @AllArgsConstructor @Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //数据库自增
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;  // MD5 密文

    @Enumerated(EnumType.STRING)        //把枚举按字符串保存
    @Column(nullable = false, length = 10)
    private Role role;

    @Column(name = "first_login", nullable = false)
    private Boolean firstLogin = true;

    /* 一对一：账号对应学生 */
    /*
        延迟加载（Lazy Loading）：当访问 Account 实体时，不会立即从数据库加载关联的 Student 实体
        只有在实际调用 getStudent() 方法访问 student 属性时，才会执行数据库查询来获取关联数据
    */
    @OneToOne(fetch = FetchType.LAZY)
    /*
        @JoinColumn 注解
        @JoinColumn 用于指定外键列的映射信息：
        name = "student_id"：指定外键列名为 student_id
        unique = true：指定该外键列具有唯一约束，确保一对一关系
        @ForeignKey 注解
        @ForeignKey 用于定义外键约束的详细信息：
        name = "fk_account_student"：指定数据库中外键约束的名称为 fk_account_student
    */
    @JoinColumn(name = "student_id", unique = true,
            foreignKey = @ForeignKey(name = "fk_account_student"))
    private Student student;

    @Column(name = "created_time", updatable = false, nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    public enum Role { STUDENT, ADMIN }

    @PrePersist
    protected void onCreate() {
        createdTime = updatedTime = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}