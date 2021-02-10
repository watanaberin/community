package com.social.community.community.aspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {
    @Pointcut("execution(* com.social.community.community.service.*.*(..))")
    public void pointcut(){

    }

    @Before("pointcut()")
    public void before(){
        System.out.println("Before");
    }
    @After("pointcut()")
    public void After(){
        System.out.println("After");
    }
    @AfterReturning("pointcut()")
    public void AfterReturning(){
        System.out.println("AfterReturning");
    }
    @AfterThrowing("pointcut()")
    public void AfterThrowing(){
        System.out.println("AfterThrowing");
    }
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("AroundBefore");
        Object obj=joinPoint.proceed();
        System.out.println("AroundAfter");

        return obj;
    }
}
