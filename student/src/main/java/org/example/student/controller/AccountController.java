package org.example.student.controller;

import org.example.student.dto.AccountDTO;
import org.example.student.dto.ChangePasswordRequest;
import org.example.student.entity.Account;
import org.example.student.service.AccountService;
import org.example.student.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:5176", "http://localhost:5177", "http://localhost:5178", "http://localhost:5179"})
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    // 修改密码接口
    @PostMapping("/change-password")
    public ResponseEntity<Result<String>> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            // 从token中提取用户名
            String username = getUsernameFromToken(token);
            
            // 调用服务修改密码
            accountService.changePassword(username, changePasswordRequest);
            
            return ResponseEntity.ok(Result.success("密码修改成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("密码修改失败: " + e.getMessage()));
        }
    }
    
    // 从JWT token中提取用户名
    private String getUsernameFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 移除 "Bearer " 前缀
        }
        return org.example.student.util.JwtUtil.getUsernameFromToken(token);
    }
    
    // 获取所有账户
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<Result<List<AccountDTO>>> getAllAccounts(HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (List<AccountDTO>) null));
        }
        
        try {
            List<Account> accounts = accountService.getAllAccounts();
            List<AccountDTO> accountDTOs = accounts.stream()
                    .map(AccountDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Result.success("获取账户列表成功", accountDTOs));
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈以便调试
            return ResponseEntity.badRequest()
                    .body(Result.error("获取账户列表失败: " + e.getMessage()));
        }
    }
    
    // 根据ID获取账户
    @RequestMapping(value = "/{id}", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<Result<AccountDTO>> getAccountById(@PathVariable Long id, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (AccountDTO) null));
        }
        
        try {
            Optional<Account> accountOpt = accountService.getAccountById(id);
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                AccountDTO accountDTO = new AccountDTO(account);
                return ResponseEntity.ok(Result.success("获取账户成功", accountDTO));
            } else {
                return ResponseEntity.badRequest()
                        .body(Result.error("账户不存在"));
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈以便调试
            return ResponseEntity.badRequest()
                    .body(Result.error("获取账户失败: " + e.getMessage()));
        }
    }
    
    // 创建或更新账户
    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public ResponseEntity<Result<AccountDTO>> saveAccount(@RequestBody Account account, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (AccountDTO) null));
        }
        
        try {
            Account savedAccount = accountService.saveAccount(account);
            AccountDTO accountDTO = new AccountDTO(savedAccount);
            return ResponseEntity.ok(Result.success("保存账户成功", accountDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("保存账户失败: " + e.getMessage()));
        }
    }
    
    // 删除账户
    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    public ResponseEntity<Result<String>> deleteAccount(@PathVariable Long id, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", "Success"));
        }
        
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.ok(Result.success("删除账户成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("删除账户失败: " + e.getMessage()));
        }
    }
}