package com.dentner.core.cmmn.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class TransactionStatVo {
    private int requestTotalCnt;
    private int requestCnt;
    private int requestIngCnt;
    private int requestEndCnt;

    private int end1HourCnt;
    private int end3HourCnt;
    private int end6HourCnt;
    private int end6HourAfterCnt;


}