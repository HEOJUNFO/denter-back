package com.dentner.core.cmmn.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Alias("MemberVo") //Alias 지정가능
public class MemberVo {

    private Integer memberNo;
    private String memberSe;
    private String memberEmail;
    private String memberPassword;
    private String memberTp;
    private String memberName;
    private int memberHpNation;
    private String memberHpNationName;
    private String memberHp;
    private String memberNickName;
    private String memberLicenseNumber;
    private String memberLicenseFile;
    private int memberLicenseFileSize;
    private String memberLicenseRealName;
    private Integer memberLicenseFileNo;
    private String memberDentistryName;
    private String memberBusinessNumber;
    private String memberAddress;
    private String memberDetailAddress;
    private String memberFirstName;
    private String memberLastName;
    private String memberAlarmAt;
    private String memberAlarmSe;
    private String memberBusinessName;
    private String memberBusinessFile;
    private int memberBusinessFileSize;
    private String memberBusinessRealName;
    private Integer memberBusinessFileNo;
    private String memberRepresentativeName;
    private String memberJobSe;
    private String memberJobSeName;
    private int memberTimezoneNo;
    private String memberTimezoneNoName;
    private String memberApprovalSe;
    private int memberBankNo;
    private String memberAccountName;
    private String memberAccountNumber;
    private String registerDt;
    private String updatedDt;
    private String deletedDt;
    private String deletedAt;

    private String socialSe;
    private String socialSeName;

    private String swNo;
    private String swNoName;
    private String swEtc;
    private String memberProfileImage;
    private int memberMileage;

    private String memberOutDt;
    private String memberBlackDt;
    private String memberAt;

    private String memberMarketingAt;
    private String memberMarketingDt;

    private int multiProfileCnt;

    // 9.25 추가
    private String blackYn;
    private String blackDt;
    private String blackRegisterDt;
    private String memberAccountBankNo;
    private String lastLoginDt;
    private String fcmToken;
}
