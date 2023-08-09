package com.sangeng.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Category;
import com.sangeng.domain.vo.CategoryVo;
import com.sangeng.domain.vo.CategoryVo2;
import com.sangeng.domain.vo.ExcelCategoryVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.service.CategoryService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.WebUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/content/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/listAllCategory")
    public ResponseResult listAllCategory(){
        List<CategoryVo> list = categoryService.listAllCategory();
        return ResponseResult.okResult(list);
    }

    @PreAuthorize("@ps.hasPermission('content:category:export')") //自定义权限，看登录的用户是否有 这个权限，有才能访问该请求。 要实现@Service("ps") public class PermissionService {}
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        //设置下载文件的请求头
        try {
            WebUtils.setDownLoadHeader("分类.xlsx",response); //设置 响应头，excel的名字
            //获取需要导出的数据   查询到所有的分类数据
            List<Category> categoryVos = categoryService.list();

            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(categoryVos, ExcelCategoryVo.class);

            //把数据写入都excel中  write（输出流，封装实体类对象）
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("sheet_分类导出")
                    .doWrite(excelCategoryVos);  // 要导出的数据excelCategoryVos， 写到excel中
        } catch (Exception e) {
            //如果出现异常 也要相应json
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result)); // 把json写入到响应当中
        }

    }

    @GetMapping("/list")
    public ResponseResult lists(Integer pageNum,Integer pageSize,String name,String status){
        return categoryService.lists(pageNum,pageSize,name,status);
    }

    @PostMapping
    public ResponseResult add(@RequestBody CategoryVo2 categoryVo2){
        return categoryService.add(categoryVo2);
    }

    @GetMapping("/{id}")
    public ResponseResult categoryDetail(@PathVariable Long id){
        return categoryService.categoryDetail(id);
    }

    @PutMapping
    public ResponseResult updateCategory(@RequestBody CategoryVo2 categoryVo2){
        return categoryService.updateCategory(categoryVo2);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteCategoryById(@PathVariable Long id){
        categoryService.removeById(id);
        return ResponseResult.okResult();
    }
}
