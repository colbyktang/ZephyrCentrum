package com.ctang.zephyrcentrum.models.dtos;

import com.ctang.zephyrcentrum.types.Roles;

import lombok.Data;

/** 
 * A Data Transfer version of User
 * @author Colby Tang
 */

@Data
public class UserDTO {

    private String username;
    private String email;
    private Roles role;
}