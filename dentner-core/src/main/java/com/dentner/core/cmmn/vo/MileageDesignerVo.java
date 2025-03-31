package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class MileageDesignerVo {
    private Integer mileageNo;
    private String mileageSe;
    private int mileageAmount;
    private int transferAmount;
    private int expectedAmount;
    private int paymentAmount;
    private String mileageCn;
    private String mileageUnit;
    private String mileageUnitName;
    private String calculateStatus;
    private String refundStatus;
    private String payRefundStatus;
    private Integer memberNo;
    private String transferDt;
}