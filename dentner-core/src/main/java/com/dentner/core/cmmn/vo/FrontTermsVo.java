package com.dentner.core.cmmn.vo;

import lombok.Data;

@Data
public class FrontTermsVo {
    private TermsVo privateTerms;
    private TermsVo privateConsignmentTerms;
    private TermsVo marketingTerms;
    private TermsVo useTerms;
}