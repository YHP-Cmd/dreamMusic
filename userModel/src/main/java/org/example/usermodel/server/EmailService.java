package org.example.usermodel.server;

public interface EmailService {
    // 发送验证码到指定邮箱
    void sendVerificationCode(String email);
    
    // 验证验证码
    boolean verifyCode(String email, String code);
}
