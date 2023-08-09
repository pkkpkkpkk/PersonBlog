package com.sangeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.domain.entity.User;


/**
 * 用户表(User)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-08 09:57:14
 */
public interface UserMapper extends BaseMapper<User> {

    boolean selectUserNameBoolean(String userName);
    boolean selectPhoneBoolean(String phone);
    boolean selectEmailBoolean(String phone);

}
