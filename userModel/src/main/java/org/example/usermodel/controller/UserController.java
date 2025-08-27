package org.example.usermodel.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.pojo.Result;
import org.example.common.pojo.SingerType;
import org.example.common.pojo.User;
import org.example.common.pojo.UserType;
import org.example.common.util.JWTUtil;
import org.example.usermodel.mapper.UserMapper;
import org.example.usermodel.mapper.UserTypeMapper;
import org.example.usermodel.server.EmailService;
import org.example.usermodel.server.impl.UserServerImpl;
import org.example.usermodel.server.impl.UserTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserServerImpl userService;

    @Autowired
    UserTypeServiceImpl  userTypeService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    UserTypeMapper  userTypeMapper;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User body) {
        String username = body.getUsername();
        String password = body.getPassword();
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.error(400, "用户名或密码不能为空");
        }
        User user = userService.login(username, password);
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }
        if (user.getStatus().equals("已冻结")){
            return Result.error(401,"账号已被冻结,请联系管理员");
        }
        body.setPassword(null);
        body.setId(user.getId());
        String token = JWTUtil.sign(body);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Result.success("登录成功", data);
    }
    // 发送邮箱验证码
    @PostMapping("/sendVerificationCode")
    public Result<String> sendVerificationCode(@RequestBody User request) {
        String email = request.getEmail();
        if (!StringUtils.hasText(email)) {
            return Result.error(400, "邮箱地址不能为空");
        }
        // 检查邮箱格式
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            return Result.error(400, "邮箱格式不正确");
        }
        // 检查邮箱是否已被注册
        if (userService.isEmailExists(email)) {
            return Result.error(400, "该邮箱已被注册");
        }
        try {
            emailService.sendVerificationCode(email);
            return Result.success("验证码发送成功");
        } catch (Exception e) {
            return Result.error(500, "验证码发送失败：" + e.getMessage());
        }
    }
    // 用户注册
    @PostMapping("/register")
    public Result<String> register(@RequestBody User request) {
        System.out.println(request);
        // 参数验证
        if (!StringUtils.hasText(request.getUsername())) {
            return Result.error(400, "用户名不能为空");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            return Result.error(400, "密码不能为空");
        }
        if (!StringUtils.hasText(request.getVerificationCode())) {
            return Result.error(400, "验证码不能为空");
        }
        // 检查用户名是否已存在
        if (userService.isUsernameExists(request.getUsername())) {
            return Result.error(400, "用户名已存在");
        }
        // 检查邮箱是否已存在
        if (StringUtils.hasText(request.getEmail()) && userService.isEmailExists(request.getEmail())) {
            return Result.error(400, "邮箱已被注册");
        }
        // 验证邮箱验证码
        if (StringUtils.hasText(request.getEmail())) {
            if (!yzmTrue(request.getEmail(),request.getVerificationCode())) {
                return Result.error(400, "验证码错误或已过期");
            }
        }
        // 创建用户对象
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setGender(request.getGender());
        user.setTypeId("2");
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus("正常");

        System.out.println("user:"+user);
        // 保存用户
        if (userService.register(user)) {
            return Result.success("注册成功");
        } else {
            return Result.error(500, "注册失败");
        }
    }
    @GetMapping("/selectByPage")//分页查询，后续可添加条件
    public IPage selectByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) String status) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (username != null) {
            queryWrapper.eq("username", username);
        }
        if (typeId != null) {
            queryWrapper.eq("type_id", typeId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        return userMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }
    @PutMapping("/setPassword/{email}/{yzm}")
    public boolean setPassword(@RequestBody User user, @PathVariable String email, @PathVariable String yzm) {
        if (!yzmTrue(email,yzm)) {
            return false;
        }
        System.out.println("userrserss"+user);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User temp =userMapper.selectOne(queryWrapper);
        String encryptedPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        temp.setPassword(encryptedPassword);
        return userService.updateById(temp);
    }
    @GetMapping("/count")
    public int count(){
        return userService.count(null);
    }
    @GetMapping("/selectById")
    public User selectById(@RequestParam Integer id){
        return userMapper.selectById(id);
    }

    @PutMapping("/update")
    public int update(@RequestBody User user){
        String encryptedPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(encryptedPassword);

        return userMapper.updateById(user);
    }

    @PostMapping("/add")
    public boolean add(@RequestBody User user){
        if (user.getStatus()==null||user.getStatus().isEmpty()){
            user.setStatus("正常");
        }
        return userService.register(user);
    }

    @DeleteMapping("/delete")
    public int delete(@RequestParam Integer id){
        return userMapper.deleteById(id);
    }
    @GetMapping("/getEmail")
    public String getEmail(@RequestParam String username){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        return userService.getOne(queryWrapper).getEmail();
    }
    @GetMapping("/getEmailYzm/{email}")
    public boolean getEmailYzm(@PathVariable String email){
        System.out.println("emali::"+email);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("3488828693@qq.com");
        message.setTo(email);
        message.setSubject("验证码");
        String str = yzm();
        redisTemplate.opsForValue().set(email,str);
        message.setText("你的验证码是"+ str);
        javaMailSender.send(message);
        return true;
    }
    @GetMapping("/yzmTrue/{email}")
    public boolean yzmTrue(@PathVariable String email,String yzm){
        String str = redisTemplate.opsForValue().get(email);
        System.out.println("old"+yzm+"new"+str);
        System.out.println("验证码结果：：：：：："+yzm.equalsIgnoreCase(str));
        return yzm.equalsIgnoreCase(str);
    }
    //生成随机验证码
    public String yzm() {
        Random rand = new Random();
        String temp ="abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<4;i++){
            stringBuilder.append(temp.charAt(rand.nextInt(temp.length())));
        }
        return stringBuilder.toString();
    }
    @PostMapping(value = "upload", consumes = "multipart/form-data")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file){
        Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("code", 400);
            response.put("message", "文件为空");
            return response;
        }
        
        // 检查文件格式
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            response.put("code", 400);
            response.put("message", "文件名不能为空");
            return response;
        }
        
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".png")) {
            response.put("code", 400);
            response.put("message", "只支持 JPG 和 PNG 格式的图片");
            return response;
        }
        
        // 检查文件大小 (2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            response.put("code", 400);
            response.put("message", "文件大小不能超过 2MB");
            return response;
        }
        
        try {
            // 生成UUID作为文件名
            String newFilename = UUID.randomUUID() + extension;
            // 确保目录存在
            File uploadDir = new File("D:/下载/image");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            // 保存文件
            File destFile = new File(uploadDir.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(destFile);
            // 返回成功响应
            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("data", newFilename);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "文件上传失败：" + e.getMessage());
        }
        return response;
    }

    @GetMapping("/getUser")
    public User getUser(@RequestHeader("Authorization") String authorizationHeader){
        System.out.println("Authorization Header: " + authorizationHeader);
        User user=JWTUtil.getUser(authorizationHeader);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",user.getUsername());
        User temp=userService.getOne(queryWrapper);
        temp.setPassword(null);
        return temp;
    }
    @GetMapping("/getAllUserType")
    public List<SingerType> getAllUserType(){
        return userMapper.getAllType();
    }
    @GetMapping("/getUserType")
    public String getUserType(@RequestParam String username){
        return userMapper.getUserType(username);
    }
    @PutMapping("/freeze")
    public int freeze(@RequestParam Integer id){
        return userMapper.freeze(id);
    }
    @PutMapping("/unfreeze")
    public int unfreeze(@RequestParam Integer id){
        return userMapper.unfreeze(id);
    }
    @GetMapping("/page")
    public IPage<UserType> page(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String typeName
    ) {
        Page<UserType> page = new Page<>(pageNum, pageSize);
        QueryWrapper<UserType> queryWrapper = new QueryWrapper<>();
        if (typeName != null) {
            queryWrapper.like("type_name", typeName);
        }
        return userTypeMapper.selectPage(page,queryWrapper);
    }
    @PutMapping("/addType")
    public int addType(@RequestBody UserType userType){
        return userTypeMapper.insert(userType);
    }
    @PutMapping("/updateType")
    public int updateType(@RequestBody UserType userType){
        QueryWrapper<UserType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type_id",userType.getTypeId());
        return userTypeMapper.update(userType,queryWrapper);
    }
    @DeleteMapping("/deleteType")
    public int  deleteType(@RequestParam Integer id){
        QueryWrapper<UserType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type_id",id);
        return userTypeMapper.delete(queryWrapper);
    }
}
