package com.sangeng.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Retention(RetentionPolicy.RUNTIME) //@SystemLog注解 保持到 runtime阶段
@Target({ElementType.METHOD})   //指定@SystemLog注解 加在method上，表示某个method受aop切面增强
public @interface SystemLog {
    //使用aop，1.写  自定义注解 @SystemLog
    String businessName(); //属性，指定业务名称 “更新用户信息”
}
