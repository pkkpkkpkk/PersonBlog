package com.sangeng.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * 文章标签关联表(SgArticleTag)表实体类
 *
 * @author makejava
 * @since 2023-07-19 10:21:55
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sg_article_tag")
public class ArticleTag implements Serializable {
    //文章id @TableId
    private Long articleId;
    //标签id

    private Long tagId;

}
