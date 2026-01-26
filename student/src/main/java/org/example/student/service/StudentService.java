package org.example.student.service;

import org.example.student.dto.StudentRegisterRequest;
import org.example.student.dto.StudentRegisterResponse;
import org.example.student.entity.Account;
import org.example.student.entity.Major;
import org.example.student.entity.Student;
import org.example.student.service.AccountService;
import org.example.student.repository.MajorRepository;
import org.example.student.repository.StudentRepository;
import org.example.student.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private AccountService accountService;

    @Transactional
    public StudentRegisterResponse registerStudent(StudentRegisterRequest request) throws Exception {
        // 检查手机号是否重复
        if (studentRepository.existsByPhone(request.getPhone())) {
            throw new Exception("手机号已被使用");
        }

        // 查找专业
        Optional<Major> majorOpt = majorRepository.findById(request.getMajorId());
        if (!majorOpt.isPresent()) {
            throw new Exception("专业不存在");
        }
        Major major = majorOpt.get();

        // 生成学号
        String studentId = generateStudentId(major, request.getEnrollmentDate());
        
        System.out.println("准备创建学生，学号为: " + studentId);

        // 创建学生实体
        Student student = new Student();
        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setPhone(request.getPhone());
        student.setEnrollmentDate(request.getEnrollmentDate());
        student.setMajor(major);
        student.setStudentId(studentId);
        student.setSequenceNumber(extractSequenceNumber(studentId));

        // 保存学生
        student = studentRepository.save(student);
        
        System.out.println("学生保存成功，学生ID: " + student.getId());

        // 创建账户
        Account account = new Account();
        account.setUsername(studentId); // 使用学号作为用户名
        String encodedPassword = PasswordEncoder.encode("123456"); // 默认密码
        System.out.println("加密后的密码: " + encodedPassword);
        account.setPassword(encodedPassword); // 默认密码
        account.setRole(Account.Role.STUDENT);
        account.setFirstLogin(true);
        account.setStudent(student);
        
        System.out.println("准备保存账户，用户名: " + account.getUsername() + ", 密码: " + account.getPassword());

        accountService.save(account);
        
        System.out.println("账户保存成功");

        // 返回结果
        StudentRegisterResponse response = new StudentRegisterResponse();
        response.setStudentId(studentId);
        response.setMessage("注册成功");

        return response;
    }

    private String generateStudentId(Major major, java.time.LocalDate enrollmentDate) throws Exception {
        int year = enrollmentDate.getYear();
        Integer maxSequence = majorRepository.findMaxSequenceNumberByMajorIdAndYear(major.getId(), year);
        int sequence = (maxSequence == null) ? 1 : maxSequence + 1;

        // 格式化序号为3位数字，确保同一专业和年级内序号唯一
        String sequenceStr = String.format("%03d", sequence);

        // 生成学号：专业代码+入学年份+序号，格式如：CS2023001
        String studentId = major.getMajorCode().toUpperCase() + year + sequenceStr;

        // 检查学号是否已存在（理论上不应该存在）
        if (studentRepository.findByStudentId(studentId).isPresent()) {
            throw new Exception("学号生成冲突，请重试");
        }

        return studentId;
    }

    private int extractSequenceNumber(String studentId) {
        // 从学号中提取序号部分（最后3位）
        String sequenceStr = studentId.substring(Math.max(0, studentId.length() - 3));
        return Integer.parseInt(sequenceStr);
    }
    
    public List<Major> getAllMajors() {
        return majorRepository.findAll();
    }
    
    public Optional<Major> getMajorById(Long id) {
        return majorRepository.findById(id);
    }
    
    public Major saveMajor(Major major) {
        return majorRepository.save(major);
    }
    
    public void deleteMajor(Long id) {
        majorRepository.deleteById(id);
    }
    
    public boolean hasStudentsInMajor(Long majorId) {
        Optional<Major> majorOpt = majorRepository.findById(majorId);
        return majorOpt.isPresent() && !majorOpt.get().getStudents().isEmpty();
    }
}