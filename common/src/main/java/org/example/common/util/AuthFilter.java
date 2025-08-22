package org.example.common.util;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Order(1)
@WebFilter(filterName = "AuthFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();
        log.debug("uri: {}", uri);
        
        // 处理CORS预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        if (uri.startsWith("/login") || uri.startsWith("/regis")||uri.startsWith("/getEmailYzm")) {
            chain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + token);
        
        // 处理Bearer前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 移除"Bearer "前缀
        }
        
        System.out.println("Extracted Token: " + token);

        if(ObjectUtil.isEmpty(token)) {
            // token不存在，响应错误
            ResponseUtil.responseJson(response, 222, "请先登录");
            return;
        }
        boolean auth = JWTUtil.verify(token);
        if(auth) {
            // 认证成功，放行
            // 获取当前登录的用户
//            User currentUser = JWTUtil.getPayload(token, User.class);
//            request.setAttribute("currentUser", currentUser);
            chain.doFilter(request, response);
        }else {
            ResponseUtil.responseJson(response, 200,"禁止访问");
        }
    }
}