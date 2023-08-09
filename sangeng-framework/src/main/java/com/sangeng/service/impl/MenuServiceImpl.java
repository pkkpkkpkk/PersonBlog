package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.SelectById;
import com.baomidou.mybatisplus.core.injector.methods.SelectList;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Menu;
import com.sangeng.domain.entity.RoleMenu;
import com.sangeng.domain.vo.MenuTreeVo;
import com.sangeng.domain.vo.MenuVo;
import com.sangeng.domain.vo.RoleMenuTreeVo;
import com.sangeng.mapper.MenuMapper;
import com.sangeng.service.MenuService;
import com.sangeng.service.RoleMenuService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2023-07-10 10:16:01
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private MenuService menuService;
    @Override
    public List<String> selectPermsByUserId(Long id) {
        //如果是管理员，返回所有权限
        if (id == 1L) {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
//        wrapper.in(Menu::getMenuType,"C","F");
            wrapper.in(Menu::getMenuType, SystemConstants.MENU, SystemConstants.BUTTON);
            wrapper.eq(Menu::getStatus, SystemConstants.STATUS_NORMAL);
            List<Menu> menus = list(wrapper);
            List<String> perms = menus.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        }

        //否则 返回其所具有的权限
        //getBaseMapper()下面有个方法selectPermsByUserId（） 与上面同名，但不是同一个
        return getBaseMapper().selectPermsByUserId(id);
    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu> menus = null;
        //判断 当前用户是否是管理员
        if (SecurityUtils.isAdmin()) {
            //如果是  获取所有符合要求的Menu
            menus = menuMapper.selectAllRouterMenu(userId);
        } else {
            //否则    获取用户所具有的Menu
            menus = menuMapper.selectRouterMenuTreeByUserId(userId);
        }
        //构件tree         集合要转换成tree格式，并且 有children字段
        //先找出第一层的菜单，然后去找它们的子菜单 设置到children属性中
        List<Menu> menuTree = builderMenuTree(menus, 0L);
        return menuTree;
    }

    private List<Menu> builderMenuTree(List<Menu> menus, Long parentId) {
        List<Menu> menuTree = menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))      //第一层菜单(parentId=0) 3个第一层菜单
                .map(menu -> menu.setChildren(getChildren(menu, menus)))  //（为3个一级菜单）设置children    //map 对管道流中的每一个数据进行转换操作。
                .collect(Collectors.toList()); //具有tree型结构（设置了children）
        return menuTree;
    }

    /**
     * 获取传入参数menu的 子Menu集合(menus)
     * 在menus集合中找到的menu的子菜单
     * @param menu
     * @return
     */
    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
        List<Menu> childrenList = menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m -> m.setChildren(getChildren(m, menus))) //三层子菜单，递归查找
                .collect(Collectors.toList());
        return childrenList;
    }

    @Override
    public ResponseResult menuList(String menuName, String status) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Objects.nonNull(menuName), Menu::getMenuName, menuName);
        queryWrapper.eq(Objects.nonNull(status), Menu::getStatus, status);
        queryWrapper.orderByAsc(Menu::getParentId, Menu::getOrderNum);
        List<Menu> menuList = list(queryWrapper);
        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menuList, MenuVo.class);

        return ResponseResult.okResult(menuVos);
    }

    @Override
    public ResponseResult addMenu(Menu menu) {
        save(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult menuById(Long id) {
        Menu menu = getById(id);
        MenuVo menuVo2 = BeanCopyUtils.copyBean(menu, MenuVo.class);
        return ResponseResult.okResult(menuVo2);
    }

    @Override
    public ResponseResult updateMenu(Menu menu) {
        Menu parentIdMenu = getById(menu.getParentId()); //通过父id查到 Menu记录
        String parentIdMenuName = parentIdMenu.getMenuName(); //父id的菜单名称

        String menuName = menu.getMenuName(); //当前修改菜单的 菜单名称
        if (!menuName.equals(parentIdMenu.getMenuName())) { //当前菜单名称 不等于 父id的菜单名称
            updateById(menu);
            return ResponseResult.okResult();
        } else {
            Integer code = 500;
            String msg = "修改菜单" + "\'" + parentIdMenuName + "\'" + "失败，上级菜单不能选择自己";
            return new ResponseResult(code, msg);
        }

    }

    /**
//    增加用户角色时，展示菜单的 二级目录
    @Override
    public ResponseResult treeSelect() {
        //1.找出parentId=0的根菜单 存到列表中
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId, 0);
        Page<Menu> page = new Page<>();
        page(page, queryWrapper);  //根据queryWrapper查询
        //2 转成MenuTreeVo集合   Menu -> MenuTreeVo (children,id,label，parentId)
        //2.1 Menu -> MenuTreeVo (id,label，parentId)
        List<MenuTreeVo> menuTreeVos = toMenuTreeVo(page.getRecords());

        //2.2 查询所有根菜单的子菜单 for循环 找出各个children ,设置到children属性
        // 解决(children)
        //查询所有根菜单对应的子菜单集合，并且赋值给对应属性
        //从parentId != 0 (表示子菜单) 开始查，因为 parentId==0是根菜单，根菜单不可能是 某个菜单的子菜单
        for (MenuTreeVo menuTreeVo : menuTreeVos) { //从根菜单中找 子菜单
                List<MenuTreeVo> children = getTreeChildren(menuTreeVo.getId());
//                if (treeVos.size() != 0){  //当有子菜单才设置子菜单，否则不设置（不然的话，子菜单为空，将子菜单设置成了一个空地址）
                    menuTreeVo.setChildren(children);
//                }
        }
        return ResponseResult.okResult(menuTreeVos);
    }

    private List<MenuTreeVo> getTreeChildren(Long id) {
        //菜单的id 等于 根菜单id，则该条菜单 为子菜单
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId,id); //Menu表中parentId = id的数据
        queryWrapper.orderByAsc(Menu::getId); //根据id升序

        List<Menu> menus = list(queryWrapper);

//        转成MenuTreeVo集合
        List<MenuTreeVo> menuTreeVos = toMenuTreeVo(menus);
        return menuTreeVos;
    }

    private List<MenuTreeVo> toMenuTreeVo(List<Menu> lists) {
//        Menu -> MenuTreeVo (id,label，parentId)
//        解决(id,parentId)
        List<MenuTreeVo> menuTreeVos = BeanCopyUtils.copyBeanList(lists, MenuTreeVo.class);
//        解决(children,label)
        for (MenuTreeVo menuTreeVo : menuTreeVos) { //遍历上面的 copyBeanList的列表
            //        解决(label)
            if (menuTreeVo.getId()!=0){ //如果Id ！=0 才查询 （Id = 0 表示根菜单，根据0查用户，会查到null）
                Menu menu = menuService.getById(menuTreeVo.getId()); //查到对应的menu
                menuTreeVo.setLabel(menu.getMenuName());
            }
        }
        return menuTreeVos;
    }

     */

//    增加用户角色时，展示菜单的三级目录
    @Override
    public ResponseResult treeSelect() {
        //1. Menu表中的所有数据 转成MenuTreeVo集合        并设置(id,label，parentId)
        // Menu -> MenuTreeVo (id,label，parentId,children=null)
        List<MenuTreeVo> menuTreeVo = list().stream()
                .map(menu -> new MenuTreeVo(menu.getId(), menu.getMenuName(), menu.getParentId(), null))
                .collect(Collectors.toList());
        //2. 根据parent_id(参数2) 将列表menuTreeVo(参数1)建立成 3级菜单树      函数名builderMenuTree2
        List<MenuTreeVo> menuTreeVo1 = builderMenuTree2(menuTreeVo,0L);
        return ResponseResult.okResult(menuTreeVo1);
    }
    private List<MenuTreeVo> builderMenuTree2(List<MenuTreeVo> menuTreeVo, long parentId) {
        //第一层
        List<MenuTreeVo> collect = menuTreeVo.stream()
                .filter(m -> m.getParentId().equals(parentId)) //过滤出 父菜单
                //getChildren2(menuTreeVo, m1) 根据m1 在 menuTreeVo列表中 循环找子菜单(m1表示3个父菜单)
                .map(m1 -> m1.setChildren(getChildren2(menuTreeVo, m1)))  //第2,3层——循环设置children，
                .collect(Collectors.toList());
        return collect;
    }
    private List<MenuTreeVo> getChildren2(List<MenuTreeVo> menuTreeVo, MenuTreeVo m1) {
        //getChildren2(menuTreeVo, m1) 根据m1(m1表示3个父菜单) 在 menuTreeVo列表中 循环找子菜单
        List<MenuTreeVo> collect2 = menuTreeVo.stream()
                .filter(m2 -> m2.getParentId().equals(m1.getId())) //找1个父菜单的子菜单
                .map(m3 -> m3.setChildren(getChildren2(menuTreeVo, m3))) //第2,3层递归设置children，
                .collect(Collectors.toList());
        return collect2;
    }

    @Autowired
    private RoleMenuService roleMenuService;
    @Override
    public ResponseResult roleMenuTreeselectById(Long id) { //参数id是 角色id
        RoleMenuTreeVo roleMenuTreeVo = new RoleMenuTreeVo();
        //1.设置menus 把3级菜单展示出来
//        Menu -> MenuTreeVo集合
        List<MenuTreeVo> menuTreeVo = list().stream()
                .map(menu -> new MenuTreeVo(menu.getId(), menu.getMenuName(), menu.getParentId(), null))
                .collect(Collectors.toList());
        //根据parent_id(参数2) 将列表menuTreeVo(参数1)建立成 3级菜单树      函数名builderMenuTree2
        List<MenuTreeVo> menus = builderMenuTree2(menuTreeVo,0);

        roleMenuTreeVo.setMenus(menus);

        //2.设置checkedKeys（3级菜单中 用户id所具有的权限啊展示出来） 用户角色所关联的 菜单id
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId,id);
        List<RoleMenu> roleMenus = roleMenuService.list(queryWrapper);
        List<String> menuIds = roleMenus.stream()
                .map(roleMenu -> roleMenu.getMenuId().toString())
                .collect(Collectors.toList());
        roleMenuTreeVo.setCheckedKeys(menuIds);
        return ResponseResult.okResult(roleMenuTreeVo);
    }
}