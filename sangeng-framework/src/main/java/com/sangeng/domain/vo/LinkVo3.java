package com.sangeng.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkVo3 {

    private Long id;

    private String address;
    private String name;

    private String logo;

    private String description;

    private String status;
}
