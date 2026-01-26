package org.example.student.service;

import org.example.student.dto.ChangePasswordRequest;
import org.example.student.entity.Account;
import org.example.student.repository.LoginRepository;
import org.example.student.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {
    
    @Autowired
    private LoginRepository loginRepository;
    
    public Optional<Account> findByUsername(String username) {
        return loginRepository.findByUsername(username);
    }
    
    public Account save(Account account) {
        return loginRepository.save(account);
    }
    
    public List<Account> getAllAccounts() {
        return loginRepository.findAll();
    }
    
    // 获取不包含循环引用的账户列表
    public List<Account> getAllAccountsWithoutCircularReference() {
        List<Account> accounts = loginRepository.findAll();
        // 这里我们不需要特殊处理，因为JPA的懒加载机制会避免循环引用
        // 但如果仍然存在问题，可以在这里进行处理
        return accounts;
    }
    
    public Optional<Account> getAccountById(Long id) {
        return loginRepository.findById(id);
    }
    
    public Account saveAccount(Account account) {
        return loginRepository.save(account);
    }
    
    public void deleteAccount(Long id) {
        loginRepository.deleteById(id);
    }
    
    public void changePassword(String username, ChangePasswordRequest changePasswordRequest) throws Exception {
        // 查找账户
        Optional<Account> accountOpt = loginRepository.findByUsername(username);
        if (!accountOpt.isPresent()) {
            throw new Exception("账户不存在");
        }
        
        Account account = accountOpt.get();
        
        // 验证当前密码
        if (!PasswordEncoder.matches(changePasswordRequest.getCurrentPassword(), account.getPassword())) {
            throw new Exception("当前密码不正确");
        }
        
        // 验证新密码复杂度
        if (!isValidPassword(changePasswordRequest.getNewPassword())) {
            throw new Exception("新密码必须包含数字、字母、特殊字符，且长度大于7位");
        }
        
        // 更新密码
        account.setPassword(PasswordEncoder.encode(changePasswordRequest.getNewPassword()));
        account.setFirstLogin(false); // 设置为非首次登录
        
        // 保存账户
        loginRepository.save(account);
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