package com.sangeng.controller;

import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddCommentDto;
import com.sangeng.domain.entity.Comment;
import com.sangeng.service.CommentService;
import com.sangeng.utils.BeanCopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@Api(tags = "CommentController_评论",description = "评论相关接口")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/commentList") // get请求会把请求的参数附加在URL后面
    public ResponseResult commentList(Long articleId,Integer pageNum,Integer pageSize){
//        0 表示 文章评论， 1表示友链评论
        return commentService.commentList(SystemConstants.ARTICLE_COMMENT,articleId,pageNum,pageSize);
    }
//    没用swagger时，写法
//    @PostMapping("")    //post请求的请求参数都是请求体中 ,所以可以用@RequestBody 获取请求体中的参数
//    public ResponseResult addComment(@RequestBody Comment comment){
//        return commentService.addComment(comment);
//    }

//    使用swagger写法 Comment-> AddCommentDto 因为要给Comment中的属性添加注释，所以不能在参数Comment中加， 加在AddCommentDto上
    @PostMapping("")    //post请求的请求参数都是请求体中 ,所以可以用@RequestBody 获取请求体中的参数
    public ResponseResult addComment(@RequestBody AddCommentDto addCommentDto){
//        将AddCommentDto 转成  comment
        Comment comment = BeanCopyUtils.copyBean(addCommentDto, Comment.class);
        return commentService.addComment(comment);
    }

    @GetMapping("/linkCommentList")
    @ApiOperation(value = "友链评论列表",notes = "获取一页友链评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum",value = "页号"),
            @ApiImplicitParam(name = "pageSize",value = "每页大小")
    })
    public ResponseResult linkCommentList(Integer pageNum , Integer pageSize){
        return commentService.commentList(SystemConstants.LINK_COMMENT,null,pageNum,pageSize);
    }

}
