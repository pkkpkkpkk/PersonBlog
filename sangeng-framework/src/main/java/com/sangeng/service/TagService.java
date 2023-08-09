package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.TagListDto;
import com.sangeng.domain.entity.Tag;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.domain.vo.TagVo;
import com.sangeng.domain.vo.TagVo2;

import java.util.List;
public interface TagService extends IService<Tag> {
    ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto);
    ResponseResult addTag(TagVo tagVo);

    ResponseResult deleteTag(Long id);

    ResponseResult getTagInfo(Long id);

    ResponseResult updateTag(TagVo tagVo);

    List<TagVo2> listAllTag();
}
