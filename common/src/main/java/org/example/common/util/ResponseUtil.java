package org.example.common.util;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class ResponseUtil {
    public static void responseJson(HttpServletResponse response, int status, Object data) {
        try {
//            response.setHeader("Access-Control-Allow-Origin", "*");
//            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(status);

            response.getWriter().write(JSON.toJSONString(data, SerializerFeature.WriteMapNullValue));
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }
}