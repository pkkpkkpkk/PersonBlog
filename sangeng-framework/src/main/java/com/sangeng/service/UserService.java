package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.vo.UserVo2;
import com.sangeng.domain.vo.UserVo4;


/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2023-06-13 10:18:06
 */
public interface UserService extends IService<User> {

    ResponseResult userInfo();

    ResponseResult updateUserInfo(User user);

    ResponseResult register(User user);

    ResponseResult userList(Integer pageNum, Integer pageSize,
                            String userName, String phonenumber, String status);

    ResponseResult addUser(UserVo2 userVo2);

    ResponseResult userDetailById(Long id);

    ResponseResult updateUser(UserVo4 userVo4);
}
