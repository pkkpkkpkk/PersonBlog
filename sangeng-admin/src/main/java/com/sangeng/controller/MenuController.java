package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Menu;
import com.sangeng.service.MenuService;
import jdk.internal.util.xml.impl.ReaderUTF8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.KeySelector;

@RestController
@RequestMapping("/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public ResponseResult menuList(String menuName, String status ){
        return menuService.menuList(menuName,status);
    }

    @PostMapping
    public ResponseResult addMenu(@RequestBody Menu menu){
        return menuService.addMenu(menu);
    }

    @GetMapping("/{id}")
    public ResponseResult menuById(@PathVariable Long id){
        return menuService.menuById(id);
    }

    @PutMapping
    public ResponseResult  updateMenu(@RequestBody Menu menu){
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("/{menuId}")
    public ResponseResult deleteMenuById(@PathVariable Long menuId){
        menuService.removeById(menuId); //直接调用Service方法
        return ResponseResult.okResult();
    }

    @GetMapping("/treeselect")
    public ResponseResult treeSelect(){
        return menuService.treeSelect();
    }
    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult roleMenuTreeselectById(@PathVariable Long id){
        return menuService.roleMenuTreeselectById(id);
    }
}
