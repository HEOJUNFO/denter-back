package com.dentner.core.cmmn.dto;


import com.dentner.core.cmmn.vo.MemberVo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDto {
    private final String grantType;
    private final String authorizationType;
    private final String accessToken;
    private final String refreshToken;
    private final Long accessTokenExpiresIn;

    private MemberVo userInfo;
}