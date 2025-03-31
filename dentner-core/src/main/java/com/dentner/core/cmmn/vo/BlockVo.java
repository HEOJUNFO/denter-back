package com.dentner.core.cmmn.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Alias("MemberVo") //Alias 지정가능
public class BlockVo {

    private Integer blockNo;
    private String blockSe;
    private Integer memberNo;
    private Integer targetNo;
    private String memberDentistryName;
    private String memberNickName;
    private String memberProfileImage;

}
