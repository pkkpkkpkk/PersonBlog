package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.injector.methods.SelectById;
import com.baomidou.mybatisplus.core.injector.methods.SelectOne;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.TagListDto;
import com.sangeng.domain.entity.Tag;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.domain.vo.TagVo;
import com.sangeng.domain.vo.TagVo2;
import com.sangeng.mapper.TagMapper;
import com.sangeng.mapper.UserMapper;
import com.sangeng.service.TagService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto) {
        //分页查询
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(tagListDto.getName()),Tag::getName,tagListDto.getName());
        queryWrapper.eq(StringUtils.hasText(tagListDto.getRemark()),Tag::getRemark,tagListDto.getRemark());

        Page<Tag> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,queryWrapper);
        //封装数据返回
        PageVo pageVo = new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }
    @Override
    public ResponseResult addTag(TagVo tagVo) {
        Tag tag = new Tag();
        tag.setName(tagVo.getName());
        tag.setRemark(tagVo.getRemark());
        //设置创建时间
        Date date = new Date();
// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// Date time = sdf.format(d);
        tag.setCreateTime(date);
        tag.setUpdateTime(date);
        tag.setDelFlag(0);

        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
//User user = userMapper.selectById(userId); tagService可以调用userMapper方法
        tag.setCreateBy(userId);

        save(tag);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteTag(Long id) {
        removeById(id);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getTagInfo(Long id) {
        Tag tag = getById(id);
        TagVo tagVo = new TagVo();
        tagVo.setId(id);
        tagVo.setName(tag.getName());
        tagVo.setRemark(tag.getRemark());
        return ResponseResult.okResult(tagVo);
    }

    @Autowired
    private TagMapper tagMapper;
    @Override
    public ResponseResult updateTag(TagVo tagVo) {
        LambdaUpdateWrapper<Tag> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Tag::getId, tagVo.getId());
        updateWrapper.set(Tag::getName, tagVo.getName());
        updateWrapper.set(Tag::getRemark, tagVo.getRemark());
        //更新时间
        //1.创建SimpleDateFormat对象，指定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //2.获取系统当前时间
        Date now = new Date();
        //格式化为 指定时间格式 yyyy-MM-dd HH:mm:ss
        String strNow = sdf.format(now);
        Date date; //声明一个全局的变量
        try {
            date = sdf.parse(strNow);  //String转换成Date类型
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        updateWrapper.set(Tag::getUpdateTime, date);
        tagMapper.update(null, updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public List<TagVo2> listAllTag() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Tag::getId,Tag::getName);
        List<Tag> tagList = list(queryWrapper); //只是查询两个字段， 如果list（）查询所有字段
        List<TagVo2> list = BeanCopyUtils.copyBeanList(tagList, TagVo2.class);
        return list;
    }
}
