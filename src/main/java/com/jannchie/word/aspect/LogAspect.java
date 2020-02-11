package com.jannchie.word.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jannchie
 */
@Aspect
@Component
public class LogAspect {
    private final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    /**
     * 定义切点
     */
    @Pointcut("execution(* com.jannchie.word.controller..*(..))")
    public void logAspect() {
    }
    @Around(value ="logAspect()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        logger.info(
                "{}({}): {} in {}s",
                ((MethodSignature) pjp.getSignature()).getMethod().getName(),
                pjp.getArgs(),
                result,
                (System.currentTimeMillis() - start) / 1000
        );
        return result;
    }
}
