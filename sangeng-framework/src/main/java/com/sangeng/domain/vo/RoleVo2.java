package com.sangeng.domain.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 角色信息表(Role)表实体类
 *
 * @author makejava
 * @since 2023-07-10 10:24:44
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleVo2 {

    //角色名称
    private String roleName;
    //角色权限字符串
    private String roleKey;
    //显示顺序
    private Integer roleSort;
    //角色状态（0正常 1停用）
    private String status;

    private List<String> menuIds;
    private String remark;
}
