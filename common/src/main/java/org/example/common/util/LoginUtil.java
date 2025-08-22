package org.example.common.util;

public class LoginUtil {
    public static String formPwdToDbPwd(String formPwd, String salt) {
        String str = formPwd + salt;
        return HashUtil.md5(str);
    }

    public static boolean equals(String formPwd, String dbPwd, String salt){
        String password = formPwdToDbPwd(formPwd, salt);
        return password.equals(dbPwd);
    }
}