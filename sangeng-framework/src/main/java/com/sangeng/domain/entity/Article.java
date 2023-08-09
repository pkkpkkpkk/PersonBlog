package com.sangeng.domain.entity;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 文章表(Article)表实体类
 *
 * @author makejava
 * @since 2023-06-02 19:30:13
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sg_article") //关联 数据库表sg_article
@Accessors(chain = true) // getset中 set方法有返回值类型Article, 不设置set返回类型为void
public class Article {
//
// 如果不用@TableId标注主键id，则插入数据时，默认使用雪花算法生成主键id，即使数据库设置了 主键自增
    @TableId
    private Long id;
    //标题
    private String title;
    //文章内容
    private String content;
    //文章摘要
    private String summary;
    //所属分类id
    private Long categoryId;

    @TableField(exist = false) //这个字段在数据库表中不存在，不要报错
    private String categoryName;

    //缩略图
    private String thumbnail;
    //是否置顶（0否，1是）
    private String isTop;
    //状态（0已发布，1草稿）
    private String status;
    //访问量
    private Long viewCount;
    //是否允许评论 1是，0否
    private String isComment;
    @TableField(fill = FieldFill.INSERT) //插入时 自动添加创建人
    private Long createBy;
    @TableField(fill = FieldFill.INSERT) //插入时 自动添加 创建时间
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE) //插入更新时 自动添加 更新人
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE) //插入更新时 自动添加 更新时间
    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;

    public Article(Long id, long viewCount) { //构造方法，只有 id, viewCount
        this.id = id;
        this.viewCount = viewCount;
    }
}

