package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Category;
import com.sangeng.domain.vo.CategoryVo;
import com.sangeng.domain.vo.CategoryVo2;

import java.util.List;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2023-06-04 09:10:57
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();

    List<CategoryVo> listAllCategory();

    ResponseResult lists(Integer pageNum, Integer pageSize, String name, String status);

    ResponseResult add(CategoryVo2 categoryVo2);

    ResponseResult categoryDetail(Long id);

    ResponseResult updateCategory(CategoryVo2 categoryVo2);
}
