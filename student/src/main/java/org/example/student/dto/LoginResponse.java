package org.example.student.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private Object user;
    
    public static LoginResponse success(String token, Object user) {
        LoginResponse response = new LoginResponse();
        response.success = true;
        response.message = "登录成功";
        response.token = token;
        response.user = user;
        return response;
    }
    
    public static LoginResponse error(String message) {
        LoginResponse response = new LoginResponse();
        response.success = false;
        response.message = message;
        return response;
    }
}