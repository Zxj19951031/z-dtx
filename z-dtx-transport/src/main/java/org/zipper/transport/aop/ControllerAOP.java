package org.zipper.transport.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.zipper.common.exceptions.SysException;
import org.zipper.common.exceptions.errors.SystemError;
import org.zipper.common.response.RespResult;

@Configuration
@Aspect
public class ControllerAOP {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAOP.class);
    private long startTime;

    @Pointcut("execution(public * org.zipper.transport.controller..*.*(..))")
    public void point() {
    }

    @Around(value = "point()")
    public RespResult around(ProceedingJoinPoint pjp) {
        long startTime = System.currentTimeMillis();

        RespResult result;

        try {
            result = (RespResult) pjp.proceed();
            logger.info(pjp.getSignature() + "use time : " + (System.currentTimeMillis() - startTime) + " ms");
        } catch (Throwable e) {
            result = handlerException(pjp, e);
        }

        return result;
    }

    private RespResult<SysException> handlerException(JoinPoint pjp, Throwable e) {
        RespResult result;

        // 已知异常
        if (e instanceof SysException) {
            result = RespResult.error((SysException) e);
        } else {
            logger.error(pjp.getSignature() + " error ", e);
            //TODO 未知的异常，应该格外注意，可以发送邮件通知等
            result = RespResult.error(SysException.newException(SystemError.SYSTEM_ERROR, e));
        }

        return result;
    }
}
