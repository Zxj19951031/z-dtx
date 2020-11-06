package org.zipper.job.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.zipper.job.constant.SystemException;
import org.zipper.job.constant.SystemResponse;
import org.zipper.job.constant.errors.SystemError;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证开始入口
 *
 * @author zhuxj
 * @since 2020/10/21
 */
@Component
public class SystemAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static Logger logger = LoggerFactory.getLogger(SystemAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        logger.error("认证失败", e);
        SystemError error = SystemError.AUTHENTICATE_ERROR;
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.setStatus(error.getCode());
        httpServletResponse.getWriter().write(SystemResponse.error(SystemException.newException(error, e)).toString());
        httpServletResponse.getWriter().flush();
        httpServletResponse.getWriter().close();
    }
}
