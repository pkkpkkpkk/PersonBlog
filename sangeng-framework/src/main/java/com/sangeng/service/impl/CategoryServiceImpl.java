package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.entity.Category;
import com.sangeng.domain.vo.CategoryVo;
import com.sangeng.domain.vo.CategoryVo2;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.mapper.CategoryMapper;
import com.sangeng.service.ArticleService;
import com.sangeng.service.CategoryService;
import com.sangeng.utils.BeanCopyUtils;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2023-06-04 09:10:58
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private ArticleService articleService;

    @Override
    public ResponseResult getCategoryList() {
//        查分类文章

        //查询文章表，状态为已发布的文章  status=0
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        List<Article> articleList = articleService.list(articleWrapper);
        //获取文章的分类id，并且去重
        Set<Long> categoryIds = articleList.stream()
                .map(article -> article.getCategoryId())    //.map(Article::getCategoryId)  article转换成long类型的id
                .collect(Collectors.toSet());       //收集一下，转换成set 去重     set无序，无重复
//        articleList.stream()
//                .map(new Function<Article, Long>() { 在Function处 alt+回车 转成lambda表达式
//                    @Override
//                    public Long apply(Article article) {
//                        return article.getCategoryId();
//                    }
//                })
        //查询分类表
        List<Category> categories = listByIds(categoryIds);   //通过categoryIds查询 查询到两分类 java,php
        // 过滤出 分类状态=‘0’的 categoryIds
        categories = categories.stream().filter(category -> SystemConstants.STATUS_NORMAL.equals(category.getStatus()))
                .collect(Collectors.toList());
        //封装vo
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(categories, CategoryVo.class);

        return ResponseResult.okResult(categoryVos);
    }

    @Override
    public List<CategoryVo> listAllCategory() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStatus,SystemConstants.STATUS_NORMAL);
        List<Category> categoryList = list(queryWrapper);
        List<CategoryVo> categoryVoList = BeanCopyUtils.copyBeanList(categoryList, CategoryVo.class); //只填充有的
        return categoryVoList;
    }

    @Override
    public ResponseResult lists(Integer pageNum, Integer pageSize, String name, String status) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Objects.nonNull(name),Category::getName,name);
        queryWrapper.eq(Objects.nonNull(status),Category::getStatus,status);
        Page<Category> page = new Page<>(pageNum,pageSize);
        page(page,queryWrapper);

        List<CategoryVo2> categoryVo2s = BeanCopyUtils.copyBeanList(page.getRecords(), CategoryVo2.class);

        PageVo pageVo = new PageVo(categoryVo2s,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult add(CategoryVo2 categoryVo2) {
        Category category = BeanCopyUtils.copyBean(categoryVo2, Category.class);
        save(category);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult categoryDetail(Long id) {
        Category category = getById(id);
        CategoryVo2 categoryVo2 = BeanCopyUtils.copyBean(category, CategoryVo2.class);
        return ResponseResult.okResult(categoryVo2);
    }

    @Override
    public ResponseResult updateCategory(CategoryVo2 categoryVo2) {
        Category category = BeanCopyUtils.copyBean(categoryVo2, Category.class);
        updateById(category);
        return ResponseResult.okResult();
    }
}
