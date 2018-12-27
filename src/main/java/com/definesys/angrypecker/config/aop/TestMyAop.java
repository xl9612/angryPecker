package com.definesys.angrypecker.config.aop;//package com.definesys.dragon.config.aop;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.Signature;
//import org.aspectj.lang.annotation.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Aspect
//@Async
//@Component
//public class TestMyAop {
//
//    Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Pointcut("execution(public * com.definesys.dragon.controller.TestController.hello(..))")
//    public void helloPointcut(){
//    }
//
//    @After(value = "helloPointcut()")
//    public void helloAfter(JoinPoint joinPoint){
//        if (joinPoint.getArgs().length > 0){
//            StringBuilder stringBuilder = new StringBuilder();
//            for (Object obj : joinPoint.getArgs()){
//                stringBuilder.append("对象名称:"+obj.getClass().getName());
//            }
//            logger.info("joinPoint.getArgs类型:"+stringBuilder.toString());
//        }
//        Signature signature = joinPoint.getSignature();
//        logger.info("Signature信息:名称:"+signature.getName()+";DeclaringTypeName"+signature.getDeclaringTypeName()
//        +";DeclaringType:"+signature.getDeclaringType()+";Modifiers:"+signature.getModifiers());
//        logger.info("joinPoint.getTarget()"+joinPoint.getTarget());
//        logger.info("joinPoint.getKind()"+joinPoint.getKind()+"joinPoint.getThis()"+joinPoint.getThis());
//        logger.info("joinPoint.getSourceLocation()"+joinPoint.getSourceLocation()+",getStaticPart:"+joinPoint.getStaticPart());
//        logger.info(Thread.currentThread().getName()+"执行{}"+joinPoint.getClass().getName(),"helloAfter");
//    }
//
//    @Before(value = "helloPointcut()")
//    public void helloBefore(JoinPoint joinPoint){
//        System.out.println(joinPoint.getKind());
//        logger.info("自己:"+joinPoint.getThis());
//        logger.info(Thread.currentThread().getName()+"执行{}"+joinPoint.getClass().getName(),"helloBefore");
//    }
//
//    @AfterReturning("helloPointcut()")
//    public void helloAfterReturning(JoinPoint joinPoint){
//        logger.info(Thread.currentThread().getName()+"执行{}"+joinPoint.getClass().getName(),"helloAfterReturning");
//    }
//
//    @AfterThrowing(value = "helloPointcut()",throwing = "ex")
//    public void helloAfterThrowing(JoinPoint joinPoint,Exception ex){
//        logger.info(ex.getMessage()+"错误信息;"+Thread.currentThread().getName()+"异常捕获:执行{}"+joinPoint.getClass().getName(),"helloAfterThrowing");
//    }
//
//}
