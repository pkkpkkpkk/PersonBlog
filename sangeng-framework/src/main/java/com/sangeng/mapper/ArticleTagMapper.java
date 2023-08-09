package com.sangeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.domain.entity.ArticleTag;

import java.util.List;


/**
 * 文章标签关联表(SgArticleTag)表数据库访问层
 *
 * @author makejava
 * @since 2023-07-19 10:21:53
 */
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    List<ArticleTag> articleTags(Long id);
}
