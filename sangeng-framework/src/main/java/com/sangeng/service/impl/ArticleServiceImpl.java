package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.injector.methods.SelectById;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.dto.ArticleListDto;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.entity.ArticleTag;
import com.sangeng.domain.entity.Category;
import com.sangeng.domain.entity.UserRole;
import com.sangeng.domain.vo.*;
import com.sangeng.mapper.ArticleMapper;
import com.sangeng.mapper.ArticleTagMapper;
import com.sangeng.service.ArticleService;
import com.sangeng.service.ArticleTagService;
import com.sangeng.service.CategoryService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.RedisCache;
import org.hamcrest.core.IsNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Resource
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult hotArticleList() {
        //查询热门文章，封装成ResponseResult返回
//        mybatisplus内部已经封装了一些方法 比如list(), ServiceImpl父类已经实现好的方法
//        LambdaQueryWrapper是MyBatis-Plus框架中的一个查询构造器，可通过Lambda表达式来构建查询条件，简洁。
//        支持链式调用，方便拼接多个查询条件，支持排序、分页等常见的查询操作。
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //必须是正式文章
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL); //方法引用形式  status ==  0
        //按浏览量排序
        queryWrapper.orderByDesc(Article::getViewCount); //根据viewcount 访问量排序
        //最多查询10条
        Page<Article> page = new Page<>(1, 10); //第1页，每页10条
        page(page,queryWrapper);

        List<Article> articles = page.getRecords(); //封装到list中

//        System.out.println();

        List<HotArticleVo> articleVos = new ArrayList<>();
        //bean拷贝  spring
//        for (Article article : articles) {
//            HotArticleVo vo = new HotArticleVo();
//            BeanUtils.copyProperties(article,vo);  //将article中属性部分 复制到 vo
//            articleVos.add(vo);
//        }
        List<HotArticleVo> vs = BeanCopyUtils.copyBeanList(articles, HotArticleVo.class);

        return ResponseResult.okResult(vs); //封装成ResponseResult形式
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //查询条件
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper(); //封装查询判断
        //如果有categoryId且>0, 查询时要和传入的相同  比较Article::getCategoryId和category是否相等
        lambdaQueryWrapper.eq(Objects.nonNull(categoryId)&&categoryId>0,Article::getCategoryId,categoryId);
        //状态是正式发布的 0
        lambdaQueryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
        //对isTop降序  1 0
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);
        //分页查询
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page,lambdaQueryWrapper);


        List<Article> articles = page.getRecords(); //article中有categoryid
        //查询categoryName
        articles = articles.stream()
                .map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName())
                        //获取分类id，查询分类信息，获取分类名称
//                        Category category = categoryService.getById(article.getCategoryId());
//                        String name = category.getName();
                        //把分类名称设置给article
//                        article.setCategoryName(name);
//                        return article;

                )
                .collect(Collectors.toList());


//        //categoryId去查categoryName进行设置
//        for (Article article : articles) {
//            Category category = categoryService.getById(article.getCategoryId());
//            article.setCategoryName(category.getName());
//        }



        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleListVo.class);

        PageVo pageVo = new PageVo(articleListVos,page.getTotal());
//       返回形式 [rows: articleListVos  total:page.getTotal() ]
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        //根据id查询文章
        Article article = getById(id);//从数据库中查
        //从redis中获取viewCount
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        article.setViewCount(viewCount.longValue());
        //转换成VO
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        //根据分类id查询分类名
        String categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if (category!=null){
            articleDetailVo.setCategoryName(category.getName());
        }
        //封装响应返回
        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新redis中对应文章 id的浏览量
//       { key="article:viewCount" ,value=viewCount } -> viewCountMap={key,value} -> key=id,value=ViewCount
        redisCache.incrementCacheMapValue("article:viewCount",id.toString(),1);
        return ResponseResult.okResult();
    }

    @Autowired
    private ArticleTagService articleTagService;

    @Override
    @Transactional  // 事务 一次处理两个SQL 1.添加博客 2.添加 博客和标签的关联  （要完成这两步都完成，要不完成 都不完成）
    public ResponseResult add(AddArticleDto articleDto) {
        //1.添加博客
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class); //仅复制articleDto中的 Article字段属性给 article
        save(article); //插入新增加的博客 到表sg_article
        //2.添加 博客和标签的关联   ( articleDto中tags数组字段 ) tags = [1,2] 转换成ArticleTag类型，再批量保存 saveBatch
        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                // tagId是参数 表示tags = [1,2]中每一个参数，然后将每一个参数变成ArticleTag 如  1 -> [2,1]
                .collect(Collectors.toList());
        articleTagService.saveBatch(articleTags); //批量保存 articleTags是一个列表，里面有两个元素，批量保存
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult adminArticleList(Integer pageNum, Integer pageSize, ArticleListDto articleListDto) {

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
//         like 是模糊查询 ‘%王%’，  还有 在左边或者右边拼接%，可以使用likeLeft或者likeRight方法。
        queryWrapper.like(Objects.nonNull(articleListDto.getTitle()),Article::getTitle,articleListDto.getTitle());
        queryWrapper.like(Objects.nonNull(articleListDto.getSummary()),Article::getSummary,articleListDto.getSummary());

        //分页查找
        Page<Article> page = new Page<>(); // Page<Article> page = new Page<>(pageNum,pageSize);
        page.setCurrent(pageNum);
        page.setSize(pageSize);

        page(page,queryWrapper); //查询 pageNum,pageSize,articleListVo2

        List<Article> list = page.getRecords();
        List<ArticleVo2> articleVo2s = BeanCopyUtils.copyBeanList(list, ArticleVo2.class); //Article -> ArticleVo2 类型，只要部分字段

        PageVo pageVo = new PageVo(articleVo2s, page.getTotal());

        return ResponseResult.okResult(pageVo);
    }


    @Override
    public ResponseResult articleById(Long id) {
        Article article = getById(id);
        ArticleVo3 articleVo3 = BeanCopyUtils.copyBean(article, ArticleVo3.class);

        List<ArticleTag> articleTags = articleTagMapper.articleTags(id); // 查找article_id = id 的ArticleTag
        List<String> Tags = articleTags.stream()
                .map(articleTag -> articleTag.getTagId().toString())
                .collect(Collectors.toList());
        articleVo3.setTags(Tags);
        return ResponseResult.okResult(articleVo3);
    }

    @Override
    @Transactional //一次请求 需要操作两次数据库，使用事务
    public ResponseResult updateArticle(ArticleVo3 articleVo3) {
//        (处理表Article)
        //1.修改article详情
        Article article = BeanCopyUtils.copyBean(articleVo3, Article.class);
        updateById(article); //根据传入的实体类id进行更新
//        (处理表ArticleTag)
        //2.修改tag标签 (先删除完，在批量插入)
        //2.1先删除表中该articleId对应的原有的aritcle_tag记录
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId,article.getId());
        articleTagService.remove(queryWrapper); //删除符合条件的 记录
        //2.2 插入新的 文章id和标签id
        List<ArticleTag> list = articleVo3.getTags().stream()   // [2,3 4]
                .map(tagId -> new ArticleTag(articleVo3.getId(), Long.valueOf(tagId)))
                .collect(Collectors.toList());
        articleTagService.saveBatch(list); //注意批量更新的时候一定要带上 列表大小

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteById(Long id) {
        removeById(id);
        return ResponseResult.okResult();
    }
}
