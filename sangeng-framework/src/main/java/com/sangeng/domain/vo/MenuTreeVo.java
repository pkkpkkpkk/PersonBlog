package com.sangeng.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true) //开启链式编程,setter方法返回的是this（也就是对象自己），代替了默认的返回值void
public class MenuTreeVo {

    private Long id;

    private String label;
    //父菜单ID
    private Long parentId;

    private List<MenuTreeVo> children;
}
