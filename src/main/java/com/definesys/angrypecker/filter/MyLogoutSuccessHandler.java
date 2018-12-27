package com.definesys.angrypecker.filter;

import com.definesys.mpaas.common.http.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        Response responseBody = new Response();

        responseBody.setCode("100");
        responseBody.setMessage("Logout Success!");

        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
//---------------------
//作者：larger5
//来源：CSDN
//原文：https://blog.csdn.net/larger5/article/details/81063438
//版权声明：本文为博主原创文章，转载请附上博文链接！