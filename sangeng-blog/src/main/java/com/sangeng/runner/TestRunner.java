package com.sangeng.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component //要把该类注入到容器中，交给springboot容器管理
public class TestRunner implements CommandLineRunner {
//    实现接口CommandLineRunner，重写run方法（在程序初始化完后，自动执行run方法）
    @Override
    public void run(String... args) throws Exception {
        System.out.println("程序初始化");
    }
}
