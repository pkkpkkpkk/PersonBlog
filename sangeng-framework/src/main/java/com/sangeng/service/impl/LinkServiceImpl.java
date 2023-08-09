package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Link;
import com.sangeng.domain.vo.LinkVo;
import com.sangeng.domain.vo.LinkVo3;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.mapper.LinkMapper;
import com.sangeng.service.LinkService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2023-06-05 20:00:56
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Override
    public ResponseResult getAllLink() {
        //查询所有审核通过的
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> links = list(queryWrapper);   // 按照条件 查询 返回一个list
        //转换成VO
        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(links, LinkVo.class);
        //封装返回
        return ResponseResult.okResult(linkVos);
    }

    @Override
    public ResponseResult linkList(Integer pageNum, Integer pageSize, String name, String status) {
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Objects.nonNull(name),Link::getName,name);
        queryWrapper.eq(Objects.nonNull(status),Link::getStatus,status);
        Page<Link> page = new Page<>(pageNum, pageSize);
        page(page,queryWrapper);
        List<LinkVo3> linkVo3s = BeanCopyUtils.copyBeanList(page.getRecords(), LinkVo3.class);
        PageVo pageVo = new PageVo(linkVo3s, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult add(Link link) {
        Link link1 = BeanCopyUtils.copyBean(link, Link.class);
        save(link1);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult linkById(Long id) {
        Link link = getById(id);
        LinkVo3 linkVo3 = BeanCopyUtils.copyBean(link, LinkVo3.class);
        return ResponseResult.okResult(linkVo3);
    }

    @Override
    public ResponseResult updateLink(LinkVo3 linkVo3) {
        Link link = BeanCopyUtils.copyBean(linkVo3, Link.class);
        updateById(link);
        return ResponseResult.okResult();
    }
}
