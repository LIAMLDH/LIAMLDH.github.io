package org.example.student.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.example.student.entity.Account;

import java.security.Key;
import java.util.Date;

/**
 * JWT工具类
 * 
 * JWT（JSON Web Token）是一种开放标准（RFC 7519），用于在各方之间安全地传输信息。
 * 它由三部分组成：Header（头部）、Payload（载荷）和Signature（签名）。
 * 
 * 本类提供生成、解析和验证JWT token的方法。
 */
public class JwtUtil {
    // 生成用于签名的密钥，使用HS256算法
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // 设置token的过期时间（毫秒），这里是24小时
    private static final long EXPIRATION_TIME = 86400000; // 24小时
    
    /**
     * 为指定账户生成JWT token
     * 
     * @param account 账户对象，包含用户名和角色信息
     * @return 生成的JWT token字符串
     */
    public static String generateToken(Account account) {
        // 获取当前时间
        Date now = new Date();
        
        // 计算token过期时间（当前时间 + 24小时）
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        
        // 构建JWT token
        return Jwts.builder()
                // 设置主题（用户名）
                .setSubject(account.getUsername())
                // 添加声明（角色信息）
                .claim("role", account.getRole().name())
                // 设置签发时间
                .setIssuedAt(now)
                // 设置过期时间
                .setExpiration(expiryDate)
                // 使用密钥进行签名
                .signWith(key)
                // 生成紧凑的JWT字符串
                .compact();
    }
    
    /**
     * 从JWT token中提取用户名
     * 
     * @param token JWT token字符串
     * @return token中包含的用户名
     */
    public static String getUsernameFromToken(String token) {
        // 解析JWT token并提取主体（用户名）
        return Jwts.parserBuilder()
                // 设置用于验证签名的密钥
                .setSigningKey(key)
                // 构建解析器
                .build()
                // 解析token
                .parseClaimsJws(token)
                // 获取载荷内容
                .getBody()
                // 提取主体（用户名）
                .getSubject();
    }
    
    /**
     * 验证JWT token是否有效
     * 
     * @param token 待验证的JWT token字符串
     * @return 如果token有效返回true，否则返回false
     */
    public static boolean validateToken(String token) throws ExpiredJwtException, UnsupportedJwtException, 
                                                              MalformedJwtException, SignatureException {
        try {
            // 尝试解析token，如果解析成功说明token有效
            Jwts.parserBuilder()
                // 设置用于验证签名的密钥
                .setSigningKey(key)
                // 构建解析器
                .build()
                // 尝试解析token
                .parseClaimsJws(token);
            // 解析成功，token有效
            return true;
        } catch (ExpiredJwtException e) {
            // token已过期
            throw e;
        } catch (UnsupportedJwtException e) {
            // 不支持的token
            throw e;
        } catch (MalformedJwtException e) {
            // token格式错误
            throw e;
        } catch (SignatureException e) {
            // 签名错误
            throw e;
        } catch (Exception e) {
            // 其他异常
            return false;
        }
    }
}