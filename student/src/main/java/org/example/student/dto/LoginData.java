package org.example.student.dto;

import lombok.Data;
import org.example.student.entity.Account;

@Data
public class LoginData {
    private String token;
    private AccountDTO user;
    
    public LoginData(String token, Account account) {
        this.token = token;
        this.user = AccountDTO.from(account);
    }
}