package org.example.student.controller;

import org.example.student.dto.LoginData;
import org.example.student.dto.LoginRequest;
import org.example.student.entity.Account;
import org.example.student.service.AccountService;
import org.example.student.util.JwtUtil;
import org.example.student.util.PasswordEncoder;
import org.example.student.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
/*
    *ResponseEntity
    *完整的 HTTP 响应控制
    *可以控制响应的状态码（如 200、400、500 等）
    *可以设置响应头信息
    *可以定义响应体内容
    *类型安全的响应构建
    *提供了丰富的静态方法来构建不同类型的响应
    *支持泛型，确保响应数据类型安全
* */
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:5176", "http://localhost:5177", "http://localhost:5178", "http://localhost:5179", "http://localhost:5181"})
public class LoginController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<Result<LoginData>> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // 添加CORS头部到响应
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        System.out.println("登录请求 - 用户名: " + username + ", 密码: " + password);

        // 检查用户名和密码是否为空
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("用户名或密码为空");
            return ResponseEntity.badRequest()
                    .body(Result.error("用户名和密码不能为空"));
        }

        // 查找用户
        Optional<Account> accountOptional = accountService.findByUsername(username);
        if (accountOptional.isEmpty()) {
            System.out.println("用户不存在: " + username);
            return ResponseEntity.badRequest()
                    .body(Result.error("用户不存在"));
        }

        Account account = accountOptional.get();
        System.out.println("数据库中的密码: " + account.getPassword());
        System.out.println("输入的密码加密后: " + PasswordEncoder.encode(password));

        // 验证密码 - 支持明文和加密两种方式
        boolean passwordMatches = false;
        // 检查是否为加密密码（MD5值为32位十六进制字符串）
        if (account.getPassword().matches("[0-9a-fA-F]{32}")) {
            // 加密密码验证
            passwordMatches = PasswordEncoder.matches(password, account.getPassword());
            System.out.println("使用加密密码验证，结果: " + passwordMatches);
        } else {
            // 明文密码验证
            passwordMatches = password.equals(account.getPassword());
            System.out.println("使用明文密码验证，结果: " + passwordMatches);
        }

        // 验证密码
        if (!passwordMatches) {
            System.out.println("密码错误 - 用户名: " + username);
            return ResponseEntity.badRequest()
                    .body(Result.error("密码错误"));
        }

        // 生成 JWT token
        String token = JwtUtil.generateToken(account);
    
        // 创建登录数据
        LoginData loginData = new LoginData(token, account);
    
        // 创建成功响应
        Result<LoginData> result = Result.success("登录成功", loginData);
        System.out.println("登录成功，返回数据: " + result);

        // 返回成功响应
        return ResponseEntity.ok(result);
    }
    
    // 注意：这个方法现在受到JWT拦截器保护，只有携带有效token的请求才能访问
    @GetMapping("/info")
    public ResponseEntity<Result<Account>> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            // 从 Authorization 头中提取 token
            String token = authHeader.replace("Bearer ", "");
            
            // 从 token 中获取用户名
            String username = JwtUtil.getUsernameFromToken(token);
            
            // 查找用户
            Optional<Account> accountOptional = accountService.findByUsername(username);
            if (accountOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Result.error("用户不存在"));
            }
            
            Account account = accountOptional.get();
            // 清除密码字段，不返回给前端
            account.setPassword(null);
            
            return ResponseEntity.ok(Result.success("获取用户信息成功", account));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("获取用户信息失败: " + e.getMessage()));
        }
    }
    
    // 修改密码接口
    @RequestMapping(value = "/change-password", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public ResponseEntity<Result<Map<String, Object>>> changePassword(//外层是 Result 对象，用于封装统一响应格式，内层是 Map<String, Object>，作为 Result 中的 data 数据
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) Map<String, String> passwordChangeRequest,
            HttpServletRequest request) {
        /*
        * 1. @RequestHeader("Authorization")
        这是一个请求头绑定注解，用于从 HTTP 请求头中提取指定字段的值。
        作用：
        从客户端发送的 HTTP 请求头中获取 "Authorization" 字段的值
        将该值绑定到方法参数 authHeader 上
        * 2. @RequestBody
        这是一个请求体绑定注解，用于将 HTTP 请求体中的 JSON 数据绑定到 Java 对象。
        作用：
        将客户端发送的 JSON 格式请求体数据转换为 Map<String, String> 对象
        绑定到 passwordChangeRequest 参数上*/

        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", "OK");
            return ResponseEntity.ok(Result.success("OK", responseData));
        }

        try {
            // 从 Authorization 头中提取 token
            String token = authHeader.replace("Bearer ", "");
            
            // 从 token 中获取用户名
            String username = JwtUtil.getUsernameFromToken(token);
            
            // 获取请求参数
            String currentPassword = passwordChangeRequest.get("currentPassword");
            String newPassword = passwordChangeRequest.get("newPassword");
            
            // 参数校验
            if (currentPassword == null || currentPassword.isEmpty() || 
                newPassword == null || newPassword.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Result.error("当前密码和新密码不能为空"));
            }
            
            // 查找用户
            Optional<Account> accountOptional = accountService.findByUsername(username);
            if (accountOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Result.error("用户不存在"));
            }
            
            Account account = accountOptional.get();
            
            // 验证当前密码
            if (!PasswordEncoder.matches(currentPassword, account.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(Result.error("当前密码错误"));
            }
            
            // 验证新密码复杂度
            if (!isValidPassword(newPassword)) {
                return ResponseEntity.badRequest()
                        .body(Result.error("密码必须包含数字、字母、特殊字符，且长度大于7位"));
            }
            
            // 更新密码
            account.setPassword(PasswordEncoder.encode(newPassword));
            account.setFirstLogin(false); // 设置为非首次登录
            accountService.save(account);
            
            // 返回成功响应
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", "密码修改成功");
            return ResponseEntity.ok(Result.success("密码修改成功", responseData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("修改密码失败: " + e.getMessage()));
        }
    }
    
    // 用户退出登录接口
    @RequestMapping(value = "/logout", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public ResponseEntity<Result<String>> logout(HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK"));
        }
        // 退出登录不需要特殊处理，前端清除token即可
        return ResponseEntity.ok(Result.success("退出登录成功"));
    }
    
    // 验证密码复杂度
    private boolean isValidPassword(String password) {
        // 检查密码长度
        if (password.length() <= 7) {
            return false;
        }
        
        // 检查是否包含数字
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        
        // 检查是否包含字母
        if (!password.matches(".*[a-zA-Z].*")) {
            return false;
        }
        
        // 检查是否包含特殊字符
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }
        
        return true;
    }
}