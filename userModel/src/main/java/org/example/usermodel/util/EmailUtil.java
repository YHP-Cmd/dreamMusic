//package org.example.usermodel.util;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Component;
//
//import java.util.Random;
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class EmailUtil {
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    // 验证码过期时间（分钟）
//    private static final int CODE_EXPIRE_MINUTES = 5;
//
//    // 发生验证码
//    public void sendVerificationCode(String email) {
//        // 生成6位随机验证码
//        Random random = new Random();
//        StringBuilder code = new StringBuilder();
//        for (int i = 0; i < 6; i++) {
//            code.append(random.nextInt(10));
//        }
//        String verificationCode = code.toString();
//
//        // 发送邮件
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("悦享音乐平台");
//        message.setText("您的验证码是：" + verificationCode + "，有效期" + CODE_EXPIRE_MINUTES + "分钟。请勿泄露给他人。");
//        message.setFrom("2577127753@qq.com");
//        mailSender.send(message);
//
//        // 将验证码存储到Redis，设置过期时间
//        String redisKey = "verification_code:" + email;
//        redisTemplate.opsForValue().set(redisKey, verificationCode, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
//    }
//
//    // 验证验证码
//    public boolean verifyCode(String email, String code) {
//        String redisKey = "verification_code:" + email;
//        String storedCode = redisTemplate.opsForValue().get(redisKey);
//
//        if (storedCode != null && storedCode.equals(code)) {
//            // 验证成功后删除验证码
//            redisTemplate.delete(redisKey);
//            return true;
//        }
//        return false;
//    }
//}
