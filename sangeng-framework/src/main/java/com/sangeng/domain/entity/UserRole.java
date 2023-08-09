package com.sangeng.domain.entity;


import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 用户和角色关联表(UserRole)表实体类
 *
 * @author makejava
 * @since 2023-07-28 10:39:57
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user_role")
public class UserRole {
//    标注联合主键
//    MyBatis-Plus 可以使用 @TableId 注解的 value 属性来指定联合主键由多个字段组成。
// 联合主键，type设置为IdType.INPUT表示主键由程序指定而非数据库生成
//    @TableId(value = "user_id,role_id",type = IdType.INPUT)
//   联合主键，都是从user表 role表中拿到的 主键id,所以这个表不能设置主键自增
    private Long userId;
    private Long roleId;

}
