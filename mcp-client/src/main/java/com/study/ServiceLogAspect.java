package com.study;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class ServiceLogAspect {
    /**
     * 切面 .. 匹配当前包和子包中的类
     *      * 匹配当前包或子包下的class
     *      * class 下的方法名
     *      .. 方法传参
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.study.controller..*.*(..))")
    public Object serviceLog(@NotNull ProceedingJoinPoint joinPoint) throws Throwable {
        long begin = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            String point = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
            long end = System.currentTimeMillis();
            long takeTime = end - begin;

            // 记录方法执行信息
            log.info("Method {} executed in {} ms",
                    joinPoint.getSignature().toShortString(), takeTime);

            return result; // 返回正确结果
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            long takeTime = end - begin;

            // 记录异常信息
            log.error("Method {} threw exception after {} ms: {}",
                    joinPoint.getSignature().toShortString(), takeTime, e.getMessage());
            throw e; // 重新抛出异常
        }
    }

}
