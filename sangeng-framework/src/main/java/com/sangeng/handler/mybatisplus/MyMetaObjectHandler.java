package com.sangeng.handler.mybatisplus;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sangeng.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {//实现MetaObjectHandler
    //配置自动填充 拦截器
    //insert操作时填充方法
    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = null;
//        try {
            userId = SecurityUtils.getUserId(); //从token中拿到userid,自动填充
//        } catch (Exception e) {     e.printStackTrace();
//        注册的时候，不能获取当前userid，所以userid设置为-1
//           userId = -1L;//表示是自己创建
//        }
        this.setFieldValByName("createTime", new Date(), metaObject);   //创建时间
        this.setFieldValByName("createBy",userId , metaObject);         //创建人
        this.setFieldValByName("updateTime", new Date(), metaObject);   //更新时间
        this.setFieldValByName("updateBy", userId, metaObject);         //更新人
    }
    //update操作时填充方法
    @Override
    public void updateFill(MetaObject metaObject) { //填充 更新时间
        this.setFieldValByName("updateTime", new Date(), metaObject);
//        this.setFieldValByName(" ", SecurityUtils.getUserId(), metaObject);
    }
}