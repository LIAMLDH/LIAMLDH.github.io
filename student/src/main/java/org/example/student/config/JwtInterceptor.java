package org.example.student.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.student.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT认证拦截器
 * 用于拦截需要认证的请求，验证JWT token的有效性
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    /**
     * 在控制器方法执行之前执行
     * 
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param handler  被调用的处理器
     * @return true表示继续执行后续操作，false表示中断执行
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, 
                             HttpServletResponse response, 
                             Object handler) throws Exception {
        
        // 获取请求方法
        String method = request.getMethod();
        
        // 如果是OPTIONS预检请求，直接放行
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        
        // 获取请求路径
        String requestURI = request.getRequestURI();
        
        // 如果是登录、注册等公开接口，直接放行
        if (requestURI.contains("/user/login") || 
            requestURI.contains("/user/register")) {
            return true;
        }
        
        // 从请求头中获取Authorization字段
        String authHeader = request.getHeader("Authorization");
        
        // 如果没有Authorization头，返回401未授权错误
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"未提供有效的认证令牌\"}");
            return false;
        }
        
        // 提取JWT token（去除"Bearer "前缀）
        String token = authHeader.substring(7);
        
        try {
            // 验证token有效性
            if (JwtUtil.validateToken(token)) {
                // token有效，放行请求
                return true;
            } else {
                // token无效，返回401错误
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"code\":401,\"message\":\"认证令牌无效或已过期\"}");
                return false;
            }
        } catch (ExpiredJwtException e) {
            // token已过期
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"认证令牌已过期\"}");
            return false;
        } catch (UnsupportedJwtException e) {
            // 不支持的token
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"不支持的认证令牌格式\"}");
            return false;
        } catch (MalformedJwtException e) {
            // token格式错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"认证令牌格式错误\"}");
            return false;
        } catch (SignatureException e) {
            // 签名错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"认证令牌签名无效\"}");
            return false;
        } catch (Exception e) {
            // 其他异常
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"认证令牌验证失败\"}");
            return false;
        }
    }
}