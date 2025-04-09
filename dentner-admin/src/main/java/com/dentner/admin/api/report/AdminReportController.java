package com.dentner.admin.api.report;


import com.dentner.admin.version.V1ApiVersion;
import com.dentner.core.cmmn.dto.AdminReportDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.AdminReportListVo;
import com.dentner.core.cmmn.vo.AdminReportVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "관리자 신고 관리", description = "관리자 신고 API")
public class AdminReportController implements V1ApiVersion {

	@Resource(name= "adminReportService")
    AdminReportService adminReportService;

    @GetMapping("/report")
    @ResponseMessage("신고 목록 조회 성공")
    @Operation(summary = "신고  목록", description = "신고 회원 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "searchKeyword", description = "검색어 ID / 닉네임 / 이름 / 신고내용", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색시작일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색종료일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "statusFilter", description = "전체 / 비활성화 / 활성화 필터", example = "",  schema = @Schema(type = "string"))
    })
    public AdminReportListVo getReportList(@ModelAttribute AdminReportDto adminReportDto) {
        return adminReportService.getReportList(adminReportDto);
    }

    @GetMapping("/report/detail/{reportNo}")
    @ResponseMessage("신고 상세 조회 성공")
    @Operation(summary = "신고 상세", description = "신고 상세를 조회한다.")
    @Parameter(name = "reportNo", description = "신고 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public AdminReportVo getReportDetail(@PathVariable("reportNo") int reportNo) {
        return adminReportService.getReportDetail(reportNo);
    }

    @PutMapping("/report/block/{memberNoArr}/{type}")
    @ResponseMessage("활성화 & 비활성화 수정 성공")
    @Operation(summary = "활성화 & 비활성화 수정", description = "활성화 & 비활성화를 수정한다.")
    @Parameter(name = "memberNoArr", description = "신고 NO", example = "", schema = @Schema(type = "String", format = ""))
    public int putReportBlock(@PathVariable("memberNoArr") String memberNoArr, @PathVariable("type") String type) {
        return adminReportService.putReportBlock(memberNoArr, type);
    }

}
