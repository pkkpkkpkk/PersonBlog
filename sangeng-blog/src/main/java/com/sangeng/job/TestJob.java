package com.sangeng.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class TestJob {

    @Scheduled(cron = "0/5 * * * * ?")    //  cron = 0/5 * * * * ? 代表从0秒开始，每隔5秒执行一次testJob()。 秒 分 时 日 月 星期
    //@Scheduled指定该方法是定时任务要执行的方法，cron属性 来 定时(指定何时)
    public void testJob(){
        //定时任务 要执行的代码
        System.out.println("定时任务执行了");
    }

}
