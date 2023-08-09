package com.sangeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.domain.entity.ArticleTag;
import com.sangeng.mapper.ArticleTagMapper;
import com.sangeng.service.ArticleTagService;
import org.springframework.stereotype.Service;

/**
 * 文章标签关联表(SgArticleTag)表服务实现类
 *
 * @author makejava
 * @since 2023-07-19 10:21:56
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {

}
