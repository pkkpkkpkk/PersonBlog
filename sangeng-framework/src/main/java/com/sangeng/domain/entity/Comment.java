package com.sangeng.domain.entity;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 评论表(Comment)表实体类
 *
 * @author makejava
 * @since 2023-06-13 08:51:54
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sg_comment")

public class Comment  {
    @TableId
    private Long id;

    //评论类型（0代表文章评论，1代表友链评论）
    private String type;
    //文章id 对哪个文章的评论
    private Long articleId;
    //根评论id
    private Long rootId;
    //评论内容
    private String content;
//    a 回复了 sangeng(对应的userid即为 toCommentUserId) 根评论默认为 -1
    //所回复的目标评论的userid
    private Long toCommentUserId;
    //回复目标评论id  回复这条评论的id 即为toCommentId
    private Long toCommentId;
    @TableField(fill = FieldFill.INSERT) //插入操作时更新
    private Long createBy;
    @TableField(fill = FieldFill.INSERT) //插入操作时更新
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE) //插入 更新 时填充
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE) //插入 更新 时填充
    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;



}
