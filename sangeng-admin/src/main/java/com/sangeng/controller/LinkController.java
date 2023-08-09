package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Link;
import com.sangeng.domain.vo.LinkVo;
import com.sangeng.domain.vo.LinkVo2;
import com.sangeng.domain.vo.LinkVo3;
import com.sangeng.service.LinkService;
import org.apache.logging.log4j.message.ReusableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/list")
    public ResponseResult linkList(Integer pageNum, Integer pageSize,String name,String status){
        return linkService.linkList(pageNum,pageSize,name,status);
    }

    @PostMapping
    public ResponseResult add(@RequestBody Link link){
        return linkService.add(link);
    }

    @GetMapping("/{id}")
    public ResponseResult linkById(@PathVariable Long id){
        return linkService.linkById(id);
    }

    @PutMapping
    public ResponseResult updateLink(@RequestBody LinkVo3 linkVo3){
        return linkService.updateLink(linkVo3);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteLinkById(@PathVariable Long id){
        linkService.removeById(id);
        return ResponseResult.okResult();
    }
}
