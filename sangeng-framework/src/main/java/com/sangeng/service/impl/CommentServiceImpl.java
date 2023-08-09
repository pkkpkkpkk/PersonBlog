package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Comment;
import com.sangeng.domain.vo.CommentVo;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.mapper.CommentMapper;
import com.sangeng.service.CommentService;
import com.sangeng.service.UserService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2023-06-13 08:51:56
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;
    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {

        //查询对应文章的根评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //对应文章(articleId)    常量.equals(变量) 常量要到前面，（如果变量写到前面，变量为空的时候，会报空指针异常）
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType),Comment::getArticleId,articleId);//第一个参数返回true时，才执行eq判断
         //根评论 rootId= -1
        queryWrapper.eq(Comment::getRootId,-1);

        //评论类型
        queryWrapper.eq(Comment::getType,commentType);

        //分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize);
        page(page,queryWrapper);  //当前只是查根评论

//        page.getRecords() //返回list<comment>集合，实际返回的list元素,还包含其他 参数，所以要再封装一下
//        List<CommentVo> commentVoList = BeanCopyUtils.copyBeanList(page.getRecords(), CommentVo.class);

//        封装成一个方法，统一处理
        List<CommentVo> commentVoList = toCommentVoList(page.getRecords());

        //查询所有根评论对应的子评论集合，并且赋值给对应属性
        for (CommentVo commentVo : commentVoList) {
            //查询对应子评论（通过根评论id查）
            List<CommentVo> children = getChildren(commentVo.getId());//评论id 等于 根评论id，则该条评论 为子评论
            commentVo.setChildren(children);
            //赋值

        }

        return ResponseResult.okResult(new PageVo(commentVoList,page.getTotal()));
    }

    /**
     * 根据根评论的id 查询所对应子评论的集合（查询所有子评论）
     * @param id 根评论id
     * @return
     */
    private List<CommentVo> getChildren(Long id) {
        //评论id 等于 根评论id，则该条评论 为子评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId,id);
        //对子评论排序
        queryWrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> comments = list(queryWrapper); //IService中的方法list()

//        转换成CommentVo的集合   comments -> CommentVo(多添加了两个属性)
        List<CommentVo> commentVos = toCommentVoList(comments);//用写好的方法toCommentVoList，如用BeanCopyUtils.copyBeanList会缺少两个属性值
        return commentVos;
    }

    /**
     *  将 子评论 coment -> CommentVo
     * @param list 子评论list列表
     * @return
     */
    private List<CommentVo>  toCommentVoList(List<Comment> list){
//        coment -> CommentVo(新添加两个属性 username 回复 toCommentUserName)
        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(list, CommentVo.class);
        //给新加属性赋值（用户名） String username； （给谁回复）   private Long toCommentUserName;

//    username 回复了 toCommentUserName（toCommentUserId）  （回复的这条评论叫做toCommentId）

        //遍历CommentVo集合
        for (CommentVo commentVo : commentVos) {
            //通过creatcommentVos = {ArrayList@9018}  size = 10eBy查询用户user的昵称 并赋值
//            拿createBy查username
            String nickName = userService.getById(commentVo.getCreateBy()).getNickName();
            commentVo.setUsername(nickName);
            //通过toCommentUserId查询toCommentUserName用户的昵称 并赋值 （从root_id != -1 也就是不是根评论 开始查，因为根评论不可能是某个评论的子评论）
            //如果toCommentUserId ！=-1 才查询 （toCommentUserId = -1 表示根评论，根据-1查用户，会查到null）
            if (commentVo.getToCommentUserId()!=-1){
                String toCommentUsername = userService.getById(commentVo.getToCommentUserId()).getNickName();
                commentVo.setToCommentUserName(toCommentUsername);
            }
        }
        return commentVos;
    }

    @Override
    public ResponseResult addComment(Comment comment) {
        //评论内容不能为空
        if (!StringUtils.hasText(comment.getContent())){
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }

//        创建人，创建时间，更新人，更新时间，用mybatis-plus自动填充
//        @tableFiled createBy，createTime， updateBy，updateTime 这四个字段 mybatisplus自动帮我们填充了
        save(comment); //IService层的方法 save
        return ResponseResult.okResult();
    }
}
