package com.dentner.core.cmmn.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Alias("MemberVo") //Alias 지정가능
public class MemberTypeVo {
    private Integer memberTypeNo;
    private Integer memberNo;
    private int memberMiddleValue;
    private int typeAmount;
    private int typeDollarAmount;
    private int typeAddAmount;
    private int typeAddDollarAmount;

}
