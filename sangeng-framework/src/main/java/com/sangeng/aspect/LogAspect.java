package com.sangeng.aspect;
import com.alibaba.fastjson.JSON;
import com.sangeng.annotation.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
@Component //切面类要注入spring容器
@Aspect    //告诉spring这是 一个切面类
@Slf4j  // log日志
public class LogAspect {
    //使用aop，2. 定义切面类LogAspect
//切面 = 切点 + 通知

    // 2.1确定切点（重点：在方法上加注解，指定切点）  注解方式（写自定义注解@SystemLog的全类名）
    @Pointcut("@annotation(com.sangeng.annotation.SystemLog)") //自定义注解SystemLog的 全类名
    public void pt(){
//    被增强方法,目标方法：controller中 @PutMapping("/userInfo")对应的方法
//joinPoint:被增强方法(目标方法)的信息 封装成一个对象
    }

    // 2.2通知方法 (增强的代码)
    @Around("pt()")  //环绕通知：指定使用的切点---指定pt()方法上所使用的切点
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable { //joinPoint:被增强方法(目标方法)的信息 封装成一个对象
//        环绕通知是前置,执行,异常,最终四个通知

        Object ret; //Around必须调用proceed()让目标方法执行，不然目标方法不会执行
        try {
            handleBefore(joinPoint);//目标方法之前 执行
            ret = joinPoint.proceed();  //这个异常，这里不能用try-catch，否则controller层的异常都在这被捕获了，so,要往上抛出异常
            handleAfter(ret); //目标方法之后 执行    参数ret是目标方法执行后返回ResponseResult类型
        } finally { //最终 都要执行的代码：打印日志信息
            // 结束后换行
            log.info("=======End=======" + System.lineSeparator()); //System.lineSeparator()获取当前系统换行符
        }
        return ret; //目标方法执行后，要有返回值，不然目标方法没有返回值
    }
    private void handleBefore(ProceedingJoinPoint joinPoint) {
//        看到类名后面有Holder 一般表示这个类 使用ThreadLocal进行数据共享，保证多个线程之间资源的隔离
//        RequestAttributes -> ServletRequestAttributes(实现类) 可以获取request
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest(); //得到request

        //获取被增强方法上的注解对象  被增强方法：controller中 @PutMapping("/userInfo")对应的方法
        SystemLog systemLog = getSystemLog(joinPoint); // 需要joinPoint对象

        log.info("=======Start=======");
        // 打印请求 URL
        log.info("URL            : {}",request.getRequestURL()); //获取当前线程请求对象， url是在request对象当中（URL在http报文的request部分）
        // 打印描述信息  业务名字，接口名字
        log.info("BusinessName   : {}", systemLog.businessName());
        // 打印 Http method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法   类名 方法名
        log.info("Class Method   : {}.{}",joinPoint.getSignature().getDeclaringType(),
                ((MethodSignature) joinPoint.getSignature()).getName());
        // 打印请求的 IP
        log.info("IP             : {}",request.getRemoteHost());
        // 打印请求入参
        log.info("Request Args   : {}", JSON.toJSONString(joinPoint.getArgs())); //数组转成json字符串
    }

    private void handleAfter(Object ret) {
        // 打印出参
        log.info("Response       : {}",JSON.toJSONString(ret) );
    }

    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint) {
//    被增强方法,目标方法：controller中 @PutMapping("/userInfo")对应的方法
//joinPoint:被增强方法(目标方法)的信息 封装成一个对象，叫做签名Signature
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//获取目标方法
        SystemLog systemLog = signature.getMethod().getAnnotation(SystemLog.class);
        return systemLog;
    }

}
