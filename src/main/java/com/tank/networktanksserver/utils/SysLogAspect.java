package com.tank.networktanksserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class SysLogAspect
{

    @Pointcut("execution(* com.tank.networktanksserver.timedTasks.*.*(..))")
    public void TimedTasksCutPoint(){}

}
