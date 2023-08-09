package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.TagListDto;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.domain.vo.TagVo;
import com.sangeng.domain.vo.TagVo2;
import com.sangeng.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/tag")
public class TagController {
    //controller要放在启动类所在的子包，不然组件扫描不到

    @Autowired
    private TagService tagService;

    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, TagListDto tagListDto){
        return tagService.pageTagList(pageNum,pageSize,tagListDto);
    }

    @PostMapping
    public ResponseResult addTag(@RequestBody TagVo tagVo){
        //@RequestBody主要用来接收前端传递给后端的json字符串中的数据的(请求体中的数据的) POST
        return tagService.addTag(tagVo);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteTag(@PathVariable("id") Long id){
        return tagService.deleteTag(id);
    }

    @GetMapping("/{id}")
    public ResponseResult getTagInfo(@PathVariable("id") Long id){
        return tagService.getTagInfo(id);
    }

    @PutMapping()
    public ResponseResult updateTag(@RequestBody TagVo tagVo){
        return tagService.updateTag(tagVo);
    }

//    @PreAuthorize() //访问接口之前，会判断是否有（这个接口的）权限，有权限才能访问该接口，否则不能访问
    @GetMapping("/listAllTag")
    public ResponseResult listAllTag(){
        List<TagVo2> List = tagService.listAllTag();
        return ResponseResult.okResult(List);
    }
}
