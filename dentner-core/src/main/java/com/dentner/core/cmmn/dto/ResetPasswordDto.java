package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String e;
    private String email;
    private String password;
}