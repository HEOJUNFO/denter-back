package com.dentner.core.cmmn.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class MailDto {
    private String mailTo;
    private String mailSubject;
    private String mailContent;
}