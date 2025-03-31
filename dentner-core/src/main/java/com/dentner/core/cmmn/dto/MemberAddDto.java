package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class MemberAddDto {
    private Integer memberNo;
    private String memberSe;
    private String memberEmail;
    private String memberPassword;
    private String memberTp;
    private String memberName;
    private int memberHpNation;
    private String memberHp;
    private String memberNickName;
    private String memberLicenseNumber;
    private String memberRepresentativeName;
    private String memberDentistryName;
    private String memberBusinessNumber;
    private String memberAddress;
    private String memberDetailAddress;
    private String memberFirstName;
    private String memberLastName;
    private String memberAlarmAt;
    private String memberAlarmSe;
    private String memberBusinessName;
    private String memberJobSe;
    private Integer memberTimezoneNo;
    private String memberApprovalSe;

    private int memberBankNo;
    private String memberAccountName;
    private String memberAccountNumber;
    private String memberAccountBankNo;
    private String memberMarketingAt;

    private String registerDt;
    private String updatedDt;
    private String deletedDt;
    private String deletedAt;

    private String socialSe;
    private String socialUniqueKey;

    private String fileDel;
}
