package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.UserDto;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.entity.Role;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.entity.UserRole;
import com.sangeng.domain.vo.*;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.mapper.UserMapper;
import com.sangeng.service.RoleService;
import com.sangeng.service.UserRoleService;
import com.sangeng.service.UserService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2023-06-13 10:18:07
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder; //SecurityConfig中将PasswordEncoder(对密码加密处理)注入到容器中了
    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
        //根据用户id查询用户信息
        User user = getById(userId); // IService 中的方法 getById
        //封装成UserInfoVo
        UserInfoVo vo = BeanCopyUtils.copyBean(user, UserInfoVo.class); //拷贝成这个类型UserInfoVo.class
        return ResponseResult.okResult(vo);
    }
    @Override
    public ResponseResult updateUserInfo(User user) {

//        updateById(user); //根据user对象中的id，更新user中的其他字段  ， (更新所有字段)

//        根据id更新avatar，nickName，email，sex
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId,user.getId());
        updateWrapper.set(User::getAvatar,user.getAvatar());
        updateWrapper.set(User::getNickName,user.getNickName());
//        updateWrapper.set(User::getEmail,user.getEmail()); //邮箱不能修改
        updateWrapper.set(User::getSex,user.getSex());
        update(updateWrapper);         //IService中的方法     更新部分字段
        return ResponseResult.okResult();
    }


    @Override
    public ResponseResult register(User user) {
        //对数据进行非空判断   （"" 或者 null）
        if (!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        //对数据进行 是否存在判断 （用户名，邮箱是否已存在）
        if (userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if (emailExist(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

//        配置类SecurityConfig中 设置加密方式 （不使用默认加密方式）
//        @Bean
//        public PasswordEncoder passwordEncoder(){ //对比密码时 加密方式  要改成BCryptPasswordEncoder()
//            return new BCryptPasswordEncoder();
//        }

        //对密码进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());//对明文密码 进行加密，得到密文
        user.setPassword(encodePassword); //将password加密后的密文,存到user中
        //存入数据库中
        save(user);  // IService中的方法 mybatisplus提供

        return ResponseResult.okResult();
    }

    private boolean emailExist(String email) {//判断用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,email);
        return count(queryWrapper)>0; //查到count=1, 返回true,
    }

    private boolean userNameExist(String userName) {//判断用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userName);
        return count(queryWrapper)>0; //查到count=1, 返回true,
    }

    @Override
    public ResponseResult userList(Integer pageNum, Integer pageSize,
                                   String userName, String phonenumber, String status) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(Objects.nonNull(userName),User::getUserName,userName);
        queryWrapper.eq(Objects.nonNull(phonenumber),User::getPhonenumber,phonenumber);
        queryWrapper.eq(Objects.nonNull(status),User::getStatus,status);

        Page<User> page = new Page<>(pageNum,pageSize);
        page(page,queryWrapper);
        List<UserVo> userVos = BeanCopyUtils.copyBeanList(page.getRecords(), UserVo.class);

        PageVo pageVo = new PageVo(userVos,page.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    @Transactional
    public ResponseResult addUser(UserVo2 userVo2) {
        //校验
//        1.用户名不能为空，否则提示：必需填写用户名
        if (Objects.isNull(userVo2.getUserName())){
           throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
//    	  2.用户名必须之前未存在，否则提示：用户名已存在
//        if (!userMapper.selectUserNameBoolean(userVo2.getUserName())){
//            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
//        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userVo2.getUserName());
        if (getOne(queryWrapper) != null){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }

//        3.手机号必须之前未存在，否则提示：手机号已存在
//        if (!userMapper.selectPhoneBoolean(userVo2.getPhonenumber())){
//            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST)
//        }
        LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(User::getPhonenumber,userVo2.getPhonenumber());
        if (getOne(queryWrapper1) != null){
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }

//    	   4.邮箱必须之前未存在，否则提示：邮箱已存在
//        if (!userMapper.selectEmailBoolean(userVo2.getEmail())){
//            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
//        }
        LambdaQueryWrapper<User> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(User::getEmail,userVo2.getEmail());
        if (getOne(queryWrapper2) != null){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        //1.保存到 user表
        User user = BeanCopyUtils.copyBean(userVo2, User.class);
//        5.新增用户时注意密码加密存储。
        //对密码进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());//对明文密码 进行加密，得到密文
        user.setPassword(encodePassword); //将password加密后的密文,存到user中
        save(user);  //新增一条用户记录

        //2.保存到user_role表
        Long userId = user.getId();
        List<UserRole> userRoleList = userVo2.getRoleIds().stream()
                .map(roleId -> new UserRole(userId, Long.parseLong(roleId)))
                .collect(Collectors.toList());
        userRoleService.saveBatch(userRoleList);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult userDetailById(Long id) {
        UserDto userDto = new UserDto();
        //1.用户关联的角色id列表
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,id);
        List<UserRole> userRoles = userRoleService.list(queryWrapper);
        List<String> roleIds = userRoles.stream()
                .map(userRole -> userRole.getRoleId().toString())
                .collect(Collectors.toList());
        userDto.setRoleIds(roleIds);
        //2.角色列表  所有的，status=0
        LambdaQueryWrapper<Role> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Role::getStatus,0);
        List<Role> roleList = roleService.list(queryWrapper1);
        userDto.setRoles(roleList);
        //3.用户信息
        User user = getById(id);
        UserVo3 userVo31 = BeanCopyUtils.copyBean(user, UserVo3.class);
        userDto.setUser(userVo31);
        return ResponseResult.okResult(userDto);
    }

    @Override
    @Transactional
    public ResponseResult updateUser(UserVo4 userVo4) {
        //1.user表 保存用户
        User user = BeanCopyUtils.copyBean(userVo4, User.class);
        updateById(user); //通过主键id更新用户
        //2.user_role表 保存 role_id
        //2.1先删除表中该userId对应的原有的user-role记录
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,user.getId());
        userRoleService.remove(queryWrapper); //删除符合条件的 记录
        //2.2再批量插入新的
        List<UserRole> userRoleList = userVo4.getRoleIds().stream()
                .map(roleId -> new UserRole(user.getId(), Long.parseLong(roleId)))
                .collect(Collectors.toList());

        userRoleService.saveBatch(userRoleList);
        return ResponseResult.okResult();
    }
}
