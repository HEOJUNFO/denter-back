package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class PassWordResetDto {
    private String email;
    private String tokenValue;
}