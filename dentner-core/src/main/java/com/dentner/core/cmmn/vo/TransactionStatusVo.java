package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class TransactionStatusVo {
    private Integer requestFormNo;
    private String requestStatus;
    private String requestDealStatus;
    private String payStatus;

}