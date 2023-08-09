package com.sangeng.job;

import com.sangeng.domain.entity.Article;
import com.sangeng.service.ArticleService;
import com.sangeng.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UpdateViewCountJob {

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ArticleService articleService;

    @Scheduled(cron = "0 0/10 * * * ? ") //每隔10分钟执行一次
    public void updateViewCount(){
//        定时任务每隔10分钟把Redis中的浏览量更新到数据库中

        //1.获取redis中的浏览量
        Map<String, Integer> viewCountMap = redisCache.getCacheMap("article:viewCount");//得到key对应的value(map={key=id,vlaue=viewCount})


        //2.更新到数据库中(更新多条文章的 浏览量) 批量操作

        // viewCountMap = { key=id ,value=viewCount }
        //双列集合map不能直接转换成流对象，可以使用keySet(包含key) entrySet(包含key和value) 推荐
        List<Article> articles = viewCountMap.entrySet()         //转成单列集合   其中的一个个元素为(key,value),(key,value),...
                .stream()                                       //然后将 key放一个结合
                .map(entry -> new Article(Long.valueOf(entry.getKey()), entry.getValue().longValue())) //entrySet对象 -> Article对象  一对一元素类型转换用map
                                //构造方法，new Article( id,viewCount)
                .collect(Collectors.toList());

//       Map类型      viewCountMap = { (id1,viewCount1),(id2,viewCount2),...      }
//       Article类型  articles = { (id1,viewCount1),(id2,viewCount2),...      }
//        Map类型 viewCountMap -> Article类型 articles
        articleService.updateBatchById(articles); //批量操作 传入文章id的list集合
    }
}
