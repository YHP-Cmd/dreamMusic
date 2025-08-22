package org.example.common.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String typeId;
    private String gender;
    private String nickname;
    private String status;
    private String image;
    @TableField(exist = false) // 核心：标记该字段不存在于数据库表中
    private String verificationCode;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", typeId='" + typeId + '\'' +
                ", gender='" + gender + '\'' +
                ", status='" + status + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                '}';
    }
}
