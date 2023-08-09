package com.sangeng;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
@SpringBootApplication
@MapperScan("com.sangeng.mapper")  // mapper报错 要添加mapper扫描

@EnableScheduling //开启定时任务功能       或者直接加到 启动类上
@EnableSwagger2  //自动生成 API 文档
public class SanGengBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(SanGengBlogApplication.class,args);
    }
}
