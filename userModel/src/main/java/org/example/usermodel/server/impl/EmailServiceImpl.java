package org.example.usermodel.server.impl;


//import org.example.usermodel.util.EmailUtil;
import org.example.usermodel.server.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

//    @Autowired
//    private EmailUtil emailUtil;


    @Override
    public void sendVerificationCode(String email) {
//        emailUtil.sendVerificationCode(email);

    }
    
    @Override
    public boolean verifyCode(String email, String code) {
//        return emailUtil.verifyCode(email,code);
        return false;
    }
}
