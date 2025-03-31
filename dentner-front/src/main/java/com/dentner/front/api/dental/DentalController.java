package com.dentner.front.api.dental;


import com.dentner.core.cmmn.dto.DentalDto;
import com.dentner.core.cmmn.dto.DesignerDto;
import com.dentner.core.cmmn.dto.ReportDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.DentalListVo;
import com.dentner.core.cmmn.vo.DentalVo;
import com.dentner.core.cmmn.vo.DesignerListVo;
import com.dentner.core.cmmn.vo.DesignerVo;
import com.dentner.front.version.V1ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "치과기공소 api", description = "치과기공소 API")
public class DentalController implements V1ApiVersion {
	@Resource(name= "dentalService")
    DentalService dentalService;

    @GetMapping("/dental/laboratory")
    @ResponseMessage("치과기공소 목록 조회 성공")
    @Operation(summary = "치과기공소 목록 조회", description = "치과기공소 목록을 조회한다.")
    @Parameters({
        @Parameter(name = "memberAreaFilter", description = "지역필터", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "fixProstheticsFilter", description = "고정성보철물 필터", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "removableProstheticsFilter", description = "가철성보철물 필터", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "correctFilter", description = "교정 필터", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "allOnFilter", description = "AllonX 필터", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "latestFilter", description = "최신순 필터", example = "",  schema = @Schema(type = "string")),
        @Parameter(name = "interestFilter", description = "관심 치과기공소 필터", example = "",  schema = @Schema(type = "string")),
        @Parameter(name = "startRow", description = "페이징 시작번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "pageCnt", description = "페이징 카운트", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "keyword", description = "검색어", example = "",  schema = @Schema(type = "string"))
    })
    public DentalListVo getDentalLaboratoryList(@ModelAttribute DentalDto dentalDto, HttpServletRequest req) {
        return dentalService.getDentalLaboratoryList(dentalDto, req);
    }

    @GetMapping("/dental/laboratory/{memberNo}")
    @ResponseMessage("치과기공소 상세 조회 성공")
    @Operation(summary = "치과기공소 상세 조회", description = "치과기공소 상세를 조회한다.")
    @Parameter(name = "memberNo", description = "치과기공소 No", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public DentalVo getDentalLaboratoryDetail(@PathVariable("memberNo") Integer memberNo){
        return dentalService.getDentalLaboratoryDetail(memberNo);
    }

    @PostMapping("/dental/laboratory/interest/{targetNo}")
    @ResponseMessage("관심 치과기공소 추가 성공")
    @Operation(summary = "관심 치과기공소 추가", description = "관심 치과기공소를 추가한다.")
    @Parameter(name = "targetNo", description = "대상 치과기공소", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int postInterestDentalLaboratory(@PathVariable("targetNo") Integer targetNo){
        return dentalService.postInterestDentalLaboratory(targetNo);
    }

    @DeleteMapping("/dental/laboratory/interest/{targetNo}")
    @ResponseMessage("관심 치과기공소 삭제 성공")
    @Operation(summary = "관심 치과기공소 삭제", description = "관심 치과기공소를 삭제한다.")
    @Parameter(name = "targetNo", description = "대상 치과기공소", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int deleteInterestDentalLaboratory(@PathVariable("targetNo") Integer targetNo){
        return dentalService.deleteInterestDentalLaboratory(targetNo);
    }

    @PostMapping("/dental/laboratory/block/{targetNo}")
    @ResponseMessage("치과기공소 차단 추가 성공")
    @Operation(summary = "치과기공소 차단", description = "치과기공소를 차단한다.")
    @Parameter(name = "targetNo", description = "대상 치과기공소", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int postBlockDentalLaboratory(@PathVariable("targetNo") Integer targetNo){
        return dentalService.postBlockDentalLaboratory(targetNo);
    }

    @DeleteMapping("/dental/laboratory/block/{targetNo}")
    @ResponseMessage("차단 치과기공소 해제 성공")
    @Operation(summary = "차단 치과기공소 해제", description = "차단 치과기공소를 해제한다.")
    @Parameter(name = "targetNo", description = "대상 치과기공소", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int deleteBlockDentalLaboratory(@PathVariable("targetNo") Integer targetNo){
        return dentalService.deleteBlockDentalLaboratory(targetNo);
    }

    @PostMapping("/dental/laboratory/report/{targetNo}")
    @ResponseMessage("치과기공소 신고 추가 성공")
    @Operation(summary = "치과기공소 신고", description = "치과기공소를 신고한다.")
    @Parameter(name = "targetNo", description = "대상 치과기공소", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int postReportDentalLaboratory(@PathVariable("targetNo") Integer targetNo, @RequestBody ReportDto reportDto){
        return dentalService.postReportDental(targetNo, reportDto);
    }

    @GetMapping("/dental/designer")
    @ResponseMessage("치자이너 목록 조회 성공")
    @Operation(summary = "치자이너 목록 조회", description = "치자이너 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "prostheticsFilter", description = "보철종류 필터", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "latestFilter", description = "최신순 필터", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "reviewFilter", description = "리뷰순 필터", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "ratingFilter", description = "평점순 필터", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "priceFilter", description = "거래 총 금액순 필터", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "interestFilter", description = "관심 치과기공소 필터", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "startRow", description = "페이징 시작번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "pageCnt", description = "페이징 카운트", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "keyword", description = "검색어", example = "",  schema = @Schema(type = "string"))
    })
    public DesignerListVo getDentalDesignerList(@ModelAttribute DesignerDto designerDto) {
        return dentalService.getDentalDesignerList(designerDto);
    }

    @PostMapping("/dental/designer/interest/{targetNo}")
    @ResponseMessage("관심 치자이너 추가 성공")
    @Operation(summary = "관심 치자이너 추가", description = "관심 치자이너를 추가한다.")
    @Parameter(name = "targetNo", description = "대상 치자이너", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int postInterestDentalDesigner(@PathVariable("targetNo") Integer targetNo){
        return dentalService.postInterestDentalDesigner(targetNo);
    }

    @DeleteMapping("/dental/designer/interest/{targetNo}")
    @ResponseMessage("관심 치자이너 삭제 성공")
    @Operation(summary = "관심 치자이너 삭제", description = "관심 치자이너를 삭제한다.")
    @Parameter(name = "targetNo", description = "대상 치자이너", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int deleteInterestDentalDesigner(@PathVariable("targetNo") Integer targetNo){
        return dentalService.deleteInterestDentalDesigner(targetNo);
    }

    @PostMapping("/dental/designer/block/{targetNo}")
    @ResponseMessage("치자이너 차단 추가 성공")
    @Operation(summary = "치자이너 차단", description = "치자이너를 차단한다.")
    @Parameter(name = "targetNo", description = "대상 치자이너", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int postBlockDentalDesigner(@PathVariable("targetNo") Integer targetNo){
        return dentalService.postBlockDentalDesigner(targetNo);
    }

    @DeleteMapping("/dental/designer/block/{targetNo}")
    @ResponseMessage("차단 치자이너 해제 성공")
    @Operation(summary = "차단 치자이너 해제", description = "차단 치자이너를 해제한다.")
    @Parameter(name = "targetNo", description = "대상 치자이너", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int deleteBlockDentalDesigner(@PathVariable("targetNo") Integer targetNo){
        return dentalService.deleteBlockDentalDesigner(targetNo);
    }

    @PostMapping("/dental/designer/report/{targetNo}")
    @ResponseMessage("치자이너 신고 추가 성공")
    @Operation(summary = "치자이너 신고", description = "치자이너를 신고한다.")
    @Parameter(name = "targetNo", description = "대상 치자이너", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int postReportDentalDesigner(@PathVariable("targetNo") Integer targetNo, @RequestBody ReportDto reportDto){
        return dentalService.postReportDental(targetNo, reportDto);
    }

    @GetMapping("/dental/designer/{memberNo}")
    @ResponseMessage("치자이너 상세 조회 성공")
    @Operation(summary = "치자이너 상세 조회", description = "치자이너 상세를 조회한다.")
    @Parameter(name = "memberNo", description = "치자이너 No", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public DesignerVo getDentalDesignerDetail(@PathVariable("memberNo") Integer memberNo){
        return dentalService.getDentalDesignerDetail(memberNo);
    }

    @PostMapping("/dental/request/block/{targetNo}")
    @ResponseMessage("의뢰인 차단 추가 성공")
    @Operation(summary = "의뢰인 차단", description = "의뢰인을 차단한다.")
    @Parameter(name = "targetNo", description = "대상 의뢰인", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int postBlockDentalRequest(@PathVariable("targetNo") Integer targetNo){
        return dentalService.postBlockDentalRequest(targetNo);
    }

    @DeleteMapping("/dental/request/block/{targetNo}")
    @ResponseMessage("차단 의뢰인 해제 성공")
    @Operation(summary = "차단 의뢰인 해제", description = "차단 의뢰인을 해제한다.")
    @Parameter(name = "targetNo", description = "대상 의뢰인", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int deleteBlockDentalRequest(@PathVariable("targetNo") Integer targetNo){
        return dentalService.deleteBlockDentalRequest(targetNo);
    }
}
