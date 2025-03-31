package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class MemberTypeDto extends CommonDto{
    private Integer memberTypeNo;
    private Integer memberNo;
    private String amountType;
    private int memberMiddleValue;
    private int memberFirstValue;
    private int typeAmount;

}
