package com.sangeng.domain.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVo3 {
//后端 更新文章接口的 实体VO
    private Long categoryId;
    private String content;
    private Long createBy;
    private Date createTime;
    private Integer delFlag;
    private Long id;
    private String isComment;
    private String isTop;
    private String status;
    private String summary;
    private List<String> tags;
    private String thumbnail;
    private String title;
    private Long updateBy;
    private Date updateTime;
    private Long viewCount;

}

