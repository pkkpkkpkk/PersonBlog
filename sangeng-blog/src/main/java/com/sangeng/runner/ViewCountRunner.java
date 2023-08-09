package com.sangeng.runner;

import com.sangeng.domain.entity.Article;
import com.sangeng.mapper.ArticleMapper;
import com.sangeng.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ViewCountRunner implements CommandLineRunner {
//    在程序初始化完后，自动执行run方法
    @Autowired
    private ArticleMapper articleMapper;//    对数据库进行操作 所以用mapper
    @Autowired
    private RedisCache redisCache; //操作redis，用redis工具类

    @Override
    public void run(String... args) throws Exception {
//        把ViewCount浏览量写到redis中
        //1.查询博客信息 id viewCount
        List<Article> articleList = articleMapper.selectList(null); //查询所有博客信息
              //转成 map    实体类 -> map 收集操作
        Map<String, Integer> viewCountMap = articleList.stream()
                .collect(Collectors.toMap( article -> article.getId().toString(), article -> article.getViewCount().intValue()) );
//        articleList.stream()
//                .collect(Collectors.toMap(new Function<Article, String>() {
//                    @Override
//                    public String apply(Article article) {
//                        return article.getId().toString(); //redis要求存入的map类型为 Map<String, Integer>
//                    }
//                }, new Function<Article, Integer>() {
//                    @Override
//                    public Integer apply(Article article) {
//                        return article.getViewCount().intValue(); //转成integer类型，如果用 Long,会被存成 1L
//                    }
//                }));

        //2.存储到redis中 { key="article:viewCount" ,value=viewCount } 其中 viewCountMap={key,value}
        redisCache.setCacheMap("article:viewCount",viewCountMap);

    }
}
