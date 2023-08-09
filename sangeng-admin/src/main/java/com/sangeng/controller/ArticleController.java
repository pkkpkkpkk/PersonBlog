package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.dto.ArticleListDto;
import com.sangeng.domain.vo.ArticleVo3;
import com.sangeng.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping
    public ResponseResult add(@RequestBody AddArticleDto article){
        return articleService.add(article);
    }

    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, ArticleListDto articleListDto){
        return articleService.adminArticleList(pageNum,pageSize,articleListDto);
    }

    @GetMapping("/{id}")
    public ResponseResult articleById(@PathVariable("id") Long id){
        return articleService.articleById(id);
    }

    @PutMapping
    public ResponseResult updateArticle(@RequestBody ArticleVo3 articleVo3){
        return articleService.updateArticle(articleVo3);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteById(@PathVariable("id") Long id){
       return articleService.deleteById(id);
    }

}
