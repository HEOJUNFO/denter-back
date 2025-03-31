package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class PasswordDto {
    private String password;
    private String oldPassword;
    private String token;
    private Integer memberNo;
}