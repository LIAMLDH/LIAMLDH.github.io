package org.example.student.controller;

import org.example.student.dto.LoginRequest;
import org.example.student.dto.StudentRegisterRequest;
import org.example.student.dto.StudentRegisterResponse;
import org.example.student.entity.Account;
import org.example.student.entity.Major;
import org.example.student.service.AccountService;
import org.example.student.service.StudentService;
import org.example.student.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:5176"})
public class RegisterContoller {
    @Autowired
    private AccountService accountService;

    @Autowired
    private StudentService studentService;
    
    @PostMapping("/student/register")
    public ResponseEntity<Result<StudentRegisterResponse>> studentRegister(@RequestBody StudentRegisterRequest registerRequest) {
        try {
            StudentRegisterResponse response = studentService.registerStudent(registerRequest);
            return ResponseEntity.ok(Result.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("注册失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/major/list")
    public ResponseEntity<Result<List<Major>>> listMajors() {
        try {
            List<Major> majors = studentService.getAllMajors();
            return ResponseEntity.ok(Result.success("获取专业列表成功", majors));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("获取专业列表失败: " + e.getMessage()));
        }
    }
}