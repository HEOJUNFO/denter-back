package com.dentner.core.cmmn.dto;

import lombok.Data;

@Data
public class RoomDto extends CommonDto{
    private String searchKeyword;
    private String searchTp;
}
