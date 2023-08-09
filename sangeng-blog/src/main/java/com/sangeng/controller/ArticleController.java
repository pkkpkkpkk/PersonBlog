package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Article;
import com.sangeng.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/hotArticleList")
    public ResponseResult hotArticleList(){
       ResponseResult result = articleService.hotArticleList();
       return result;
    }

    @GetMapping("/articleList")
    public ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId){
        return articleService.articleList(pageNum,pageSize,categoryId);
    }

    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){
        //@PathVariable将url{id}中id传给 控制器方法参数
        return articleService.getArticleDetail(id);
    }

    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long id){
        return articleService.updateViewCount(id);
    }
}
