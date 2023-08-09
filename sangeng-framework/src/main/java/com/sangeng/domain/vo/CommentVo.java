package com.sangeng.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVo {
    //评论id
    private Long id;
    //文章id
    private Long articleId;
    //根评论id  -1 表示根评论
    private Long rootId;
    //评论内容
    private String content;
    //所回复的目标评论的userid
// 用户a 回复了 sangeng (sangeng对应的userid即为 toCommentUserId) 根评论默认为 -1
    private Long toCommentUserId;

//    username 回复了 toCommentUserName（toCommentUserId）  （回复的这条评论叫做toCommentId）

    //回复目标评论名字 （手动添加） 给谁评论
// 用户a 回复了 sangeng（sangeng 即为toCommentUserName）
    private String toCommentUserName;

    //回复目标评论id
//回复某条评论 这条评论的id 即为toCommentId
    private Long toCommentId;

    private Long createBy;

    private Date createTime;

    //评论人名字——昵称  （手动添加）   评论人的昵称
//    username 回复了 toCommentUserName（toCommentUserId）  （回复的这条评论叫做toCommentId）
// 用户a（即为username） 回复了 sangeng
    private String username;

    //子评论
    private List<CommentVo> children;

}
