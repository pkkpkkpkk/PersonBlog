package com.sangeng.domain.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "添加评论Dto")
public class AddCommentDto {
//    Dto对象 只是封装一下，在api文档中展示，所以不需要加 主键，自动添加注解

    private Long id;
    //评论类型（0代表文章评论，1代表友链评论）
    @ApiModelProperty(notes = "评论类型（0代表文章评论，1代表友链评论）")
    private String type;
    //文章id 对哪个文章的评论
    @ApiModelProperty(notes = "文章id")
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

    private Long createBy;

    private Date createTime;

    private Long updateBy;

    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;
}
