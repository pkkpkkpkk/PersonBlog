package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Link;
import com.sangeng.domain.vo.LinkVo3;


/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2023-06-05 20:00:55
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();

    ResponseResult linkList(Integer pageNum, Integer pageSize, String name, String status);

    ResponseResult add(Link link);

    ResponseResult linkById(Long id);

    ResponseResult updateLink(LinkVo3 linkVo3);
}
