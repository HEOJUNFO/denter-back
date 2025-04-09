package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class MileageCalculateAddDto {
    private Integer calculateNo;
    private Integer calculateGroupNo;
    private Integer mileageNo;
    private int calculateAmount;
    private String calculateSe;
    private String mileageUnit;
    private Integer registerNo;
    private int calculateGroupAmount;
}