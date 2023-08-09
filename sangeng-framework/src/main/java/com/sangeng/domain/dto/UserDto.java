package com.sangeng.domain.dto;

import com.sangeng.domain.entity.Role;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.vo.UserVo3;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private List<String> roleIds;
    private List<Role> roles;
    private UserVo3 user;
}
