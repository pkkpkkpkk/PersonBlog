package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.dto.ArticleListDto;
import com.sangeng.domain.vo.ArticleVo3;

public interface ArticleService extends IService<Article> {

    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult add(AddArticleDto article);

    ResponseResult adminArticleList(Integer pageNum, Integer pageSize, ArticleListDto articleListVo2);

    ResponseResult articleById(Long id);

    ResponseResult updateArticle(ArticleVo3 articleVo3);

    ResponseResult deleteById(Long id);
}
