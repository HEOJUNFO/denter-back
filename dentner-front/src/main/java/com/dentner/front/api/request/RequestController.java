package com.dentner.front.api.request;


import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.*;
import com.dentner.front.version.V1ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "의뢰서 api", description = "의뢰서 API")
public class RequestController implements V1ApiVersion {
    @Resource(name = "requestService")
    RequestService requestService;

    @PostMapping("/request/often")
    @ResponseMessage("자주쓰는 말 추가 성공")
    @Operation(summary = "자주쓰는 말 저장", description = "자주쓰는말을 저장한다.")
    @Parameters({
        @Parameter(name = "oftenCn", description = "자주쓰는말 내용", example = "" ,  schema = @Schema(type = "string"))
    })
    public int postRequestOften(@RequestBody OftenDto oftenDto) {
        return requestService.postRequestOften(oftenDto);
    }

    @GetMapping("/request/often")
    @ResponseMessage("자주쓰는 말 목록 성공")
    @Operation(summary = "자주쓰는 말 목록 조회", description = "자주쓰는 말 목록을 조회한다.")
    public List<OftenVo> getOftenList() {
        return requestService.getOftenList();
    }

    @DeleteMapping("/request/often/{oftenNo}")
    @ResponseMessage("자주쓰는 말 삭제 성공")
    @Operation(summary = "자주쓰는 말 삭제", description = "자주쓰는말을 삭제한다.")
    @Parameter(name = "oftenNo", description = "자주쓰는말 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int deleteRequestOften(@PathVariable(required = false) Integer oftenNo) {
        return requestService.deleteRequestOften(oftenNo);
    }

    @PostMapping("/request/value")
    @ResponseMessage("수치 값 추가 성공")
    @Operation(summary = "수치 값 저장", description = "수치 값을 저장한다.")
    @Parameters({
        @Parameter(name = "valueCn", description = "수치값 내용", example = "" ,  schema = @Schema(type = "string"))
    })
    public int postRequestValue(@RequestBody ValueDto valueDto) {
        return requestService.postRequestValue(valueDto);
    }

    @GetMapping("/request/value")
    @ResponseMessage("수치 값 목록 성공")
    @Operation(summary = "수치 값 목록 조회", description = "수치 값 목록을 조회한다.")
    public List<ValueVo> getValueList() {
        return requestService.getValueList();
    }

    @DeleteMapping("/request/value/{valueNo}")
    @ResponseMessage("수치 값 삭제 성공")
    @Operation(summary = "수치 값 삭제", description = "수치 값을 삭제한다.")
    @Parameter(name = "valueNo", description = "수치값 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int deleteRequestValue(@PathVariable(required = false) Integer valueNo) {
        return requestService.deleteRequestValue(valueNo);
    }

    @PostMapping("/request/simple")
    @ResponseMessage("간편의뢰서 작성 성공")
    @Operation(summary = "간편의뢰서 작성 저장", description = "간편의뢰서를 작성한다.")
    @Parameters({
            @Parameter(name = "requestNumber", description = "의뢰번호", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestProcessEtcName", description = "가공방법 기타", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestProcessNo", description = "가공방법 NO", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestDc", description = "상세설명", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "typeList", description = "보철종류 list", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "files", description = "파일", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "saveAt", description = "임시저장여부", example = "" ,  schema = @Schema(type = "string"))
    })
    public int postRequestSimple(@ModelAttribute RequestDocDto requestDocDto,
                                 @RequestParam(required = false) List<MultipartFile> files) throws Exception {
        return requestService.postRequestSimple(requestDocDto, files);
    }

    @PutMapping("/request/simple/{requestDocGroupNo}")
    @ResponseMessage("간편의뢰서 수정 성공")
    @Operation(summary = "간편의뢰서 수정 저장", description = "간편의뢰서를 수정한다.")
    @Parameters({
            @Parameter(name = "requestDocGroupNo", description = "의뢰서 NO", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "requestNumber", description = "의뢰번호", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestProcessEtcName", description = "가공방법 기타", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestProcessNo", description = "가공방법 NO", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestDc", description = "상세설명", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "typeList", description = "보철종류 list", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "files", description = "파일", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "saveAt", description = "임시저장여부", example = "" ,  schema = @Schema(type = "string"))
    })
    public int putRequestSimple(@PathVariable(required = false) Integer requestDocGroupNo,
                                @ModelAttribute RequestDocDto requestDocDto,
                                @RequestParam(required = false) List<MultipartFile> files) throws Exception {
        return requestService.putRequestSimple(requestDocGroupNo, requestDocDto, files);
    }

    @PostMapping("/request/detail")
    @ResponseMessage("상세의뢰서 작성 성공")
    @Operation(summary = "상세의뢰서 작성 저장", description = "상세의뢰서를 작성한다.")
    @Parameters({
        @Parameter(name = "requestNumber", description = "의뢰번호", example = "" ,  schema = @Schema(type = "string")),
        @Parameter(name = "docList", description = "의뢰서 list", example = "" ,  schema = @Schema(type = "string")),
        @Parameter(name = "files", description = "파일", example = "" ,  schema = @Schema(type = "string")),
        @Parameter(name = "saveAt", description = "임시저장여부", example = "" ,  schema = @Schema(type = "string"))
    })
    public int postRequestDetail(@ModelAttribute RequestDocDto requestDocDto,
                                 @RequestParam(required = false) List<MultipartFile> files) throws Exception {
        return requestService.postRequestDetail(requestDocDto, files);
    }

    @PutMapping("/request/detail/{requestDocGroupNo}")
    @ResponseMessage("상세의뢰서 수정 성공")
    @Operation(summary = "상세의뢰서 수정 저장", description = "상세의뢰서를 수정한다.")
    @Parameters({
        @Parameter(name = "requestDocGroupNo", description = "의뢰서 NO", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "requestNumber", description = "의뢰번호", example = "" ,  schema = @Schema(type = "string")),
        @Parameter(name = "docList", description = "의뢰서 list", example = "" ,  schema = @Schema(type = "string")),
        @Parameter(name = "files", description = "파일", example = "" ,  schema = @Schema(type = "string")),
        @Parameter(name = "saveAt", description = "임시저장여부", example = "" ,  schema = @Schema(type = "string"))
    })
    public int putRequestDetail(@PathVariable(required = false) Integer requestDocGroupNo,
                                @ModelAttribute RequestDocDto requestDocDto,
                                @RequestParam(required = false) List<MultipartFile> files) throws Exception {
        return requestService.putRequestDetail(requestDocGroupNo, requestDocDto, files);
    }

    @GetMapping("/request/temp/{type}")
    @ResponseMessage("임시저장 의뢰서 목록 성공")
    @Operation(summary = "임시저장 의뢰서 목록 조회", description = "임시저장 의뢰서 목록을 조회한다.")
    @Parameter(name = "type", description = "임시저장 구분", example = "" ,  schema = @Schema(type = "string"))
    public List<RequestBasketVo> getTempList(@PathVariable(required = false) String type) {
        return requestService.getTempList(type);
    }

    @GetMapping("/request/basket/{type}")
    @ResponseMessage("의뢰서 바구니 목록 성공")
    @Operation(summary = "의뢰서 바구니 목록 조회", description = "의뢰서 바구니 목록을 조회한다.")
    @Parameters({
        @Parameter(name = "type", description = "의뢰서 바구니 구분", example = "" ,  schema = @Schema(type = "string")),
        @Parameter(name = "startRow", description = "페이징 시작번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "pageCnt", description = "페이징 카운트", example = "",  schema = @Schema(type = "integer", format = "int32"))
    })
    public RequestBasketListVo getRequestBasketList(@PathVariable(required = false) String type, @ModelAttribute RequestBasketDto requestBasketDto) {
        return requestService.getRequestBasketList(type, requestBasketDto);
    }

    @PostMapping("/request/public-form")
    @ResponseMessage("공개요청서 작성 성공")
    @Operation(summary = "공개요청서 작성", description = "공개요청서를 작성한다.")
    @Parameters({
            @Parameter(name = "requestFormSj", description = "요청서 제목", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestFormSe", description = "요청서 구분", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestFormType", description = "요청서 보철타입", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestExpireDate", description = "요청 만료 날짜", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestExpireTime", description = "요청 만료 시간", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestDeadlineDate", description = "납품 마감 날짜", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestDeadlineTime", description = "납품 마감 시간", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestSw", description = "선호 CAD", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestFormDc", description = "요청사항", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestDocGroupsNo", description = "의뢰서 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    })
    public int postRequestPublicForm(@RequestBody RequestFormAddDto requestFormAddDto) {
        return requestService.postRequestPublicForm(requestFormAddDto);
    }

    @PutMapping("/request/form")
    @ResponseMessage("요청서 수정 성공")
    @Operation(summary = "요청서 수정", description = "요청서를 수정한다.")
    @Parameters({
            @Parameter(name = "requestFormSj", description = "요청서 제목", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestFormSe", description = "요청서 구분", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestFormType", description = "요청서 보철타입", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestExpireDate", description = "요청 만료 날짜", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestExpireTime", description = "요청 만료 시간", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestDeadlineDate", description = "납품 마감 날짜", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestDeadlineTime", description = "납품 마감 시간", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestSw", description = "선호 CAD", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestFormDc", description = "요청사항", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestDocGroupsNo", description = "의뢰서 NO", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "requestFormNo", description = "요청서 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    })
    public int putRequestForm(@RequestBody RequestFormAddDto requestFormAddDto) {
        return requestService.putRequestForm(requestFormAddDto);
    }

    @PostMapping("/request/target-form")
    @ResponseMessage("지정요청서 작성 성공")
    @Operation(summary = "지정요청서 작성", description = "지정요청서를 작성한다.")
    @Parameters({
            @Parameter(name = "requestFormSj", description = "요청서 제목", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestFormSe", description = "요청서 구분", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestFormType", description = "요청서 보철타입", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestDeadlineDate", description = "납품 마감 날짜", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestDeadlineTime", description = "납품 마감 시간", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestSw", description = "선호 CAD", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestFormDc", description = "요청사항", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "requestDocGroupsNo", description = "의뢰서 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    })
    public int postRequestTargetForm(@RequestBody RequestFormAddDto requestFormAddDto) {
        return requestService.postRequestTargetForm(requestFormAddDto);
    }

    @GetMapping("/request/doc/{type}")
    @ResponseMessage("의뢰서 추가하기 요청서 목록 성공")
    @Operation(summary = "의뢰서 추가하기 요청서 목록 조회", description = "의뢰서 추가하기 요청서 목록을 조회한다.")
    @Parameter(name = "type", description = "요청서 추가하기 구분", example = "" ,  schema = @Schema(type = "string"))
    public List<RequestBasketVo> getRequestDocList(@PathVariable(required = false) String type) {
        return requestService.getRequestDocList(type);
    }

    @GetMapping("/request/target-designer")
    @ResponseMessage("지정할 치자이너 목록 성공")
    @Operation(summary = "지정할 치자이너 목록 조회", description = "지정할 치자이너 목록을 조회한다.")
    public List<TargetDesignerVo> getRequestDesignerList() {
        return requestService.getRequestDesignerList();
    }

    @GetMapping("/request/form/{requestFormSe}")
    @ResponseMessage("요청서 목록 조회 성공")
    @Operation(summary = "요청서 목록 조회", description = "요청서 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "prostheticsFilter", description = "보철물 필터", example = "", schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "statusFilter", description = "진행상태 필터", example = "", schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "myFilter", description = "내요청글 필터", example = "", schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "latestFilter", description = "최신순 필터", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "dedLineFilter", description = "견적만료순 필터", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "oldFilter", description = "오래된순 필터", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "startRow", description = "페이징 시작번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "pageCnt", description = "페이징 카운트", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "keyword", description = "검색어", example = "", schema = @Schema(type = "string"))
    })
    public RequestFormListVo getRequestFormList(@PathVariable(required = false) String requestFormSe, @ModelAttribute RequestFormDto requestFormDto) {
        return requestService.getRequestFormList(requestFormSe, requestFormDto);
    }

    @GetMapping("/request/form/prosthetics/{requestFormNo}")
    @ResponseMessage("보철종류 보기 성공")
    @Operation(summary = "보철종류 보기 조회", description = "보철종류 보기를 한다.")
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public RequestFormProstheticsVo getRequestFormProsthetics(@PathVariable(required = false) Integer requestFormNo) {
        return requestService.getRequestFormProsthetics(requestFormNo);
    }

    @GetMapping("/request/form/detail/{requestFormNo}")
    @ResponseMessage("요청서 상세 조회 성공")
    @Operation(summary = "요청서 상세 조회", description = "요청서 상세를 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public RequestFormDetailVo getRequestFormDetail(@PathVariable(required = false) Integer requestFormNo) {
        return requestService.getRequestFormDetail(requestFormNo);
    }

    @PostMapping("/request/form/report/{requestFormNo}/{targetNo}")
    @ResponseMessage("요청서 작성자 신고 추가 성공")
    @Operation(summary = "요청서 작성자 신고", description = "요청서 작성자를 신고한다.")
    @Parameter(name = "targetNo", description = "대상 의뢰인", example = "", schema = @Schema(type = "integer", format = "int32"))
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int postReportRequestForm(@PathVariable("requestFormNo") Integer requestFormNo, @PathVariable("targetNo") Integer targetNo, @RequestBody ReportDto reportDto) {
        return requestService.postReportRequestForm(requestFormNo, targetNo, reportDto);
    }

    @PostMapping("/request/form/reply")
    @ResponseMessage("요청서 댓글 작성 성공")
    @Operation(summary = "요청서 댓글 작성", description = "요청서 댓글을 작성한다.")
    @Parameters({
            @Parameter(name = "requestFormNo", description = "요청서 NO", example = "", schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "answerCn", description = "댓글 내용", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "parentAnswerNo", description = "댓글 상위 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    })
    public int postRequestFormReply(@RequestBody ReplyAddDto replyAddDto) throws Exception{
        return requestService.postRequestFormReply(replyAddDto);
    }

    @PutMapping("/request/form/reply/{requestFormAnswerNo}")
    @ResponseMessage("요청서 댓글 수정 성공")
    @Operation(summary = "요청서 댓글 수정", description = "요청서 댓글을 수정한다.")
    @Parameters({
            @Parameter(name = "requestFormAnswerNo", description = "댓글 NO", example = "", schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "requestFormNo", description = "요청서 NO", example = "", schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "answerCn", description = "댓글 내용", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "parentAnswerNo", description = "댓글 상위 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    })
    public int putRequestFormReply(@PathVariable(required = false) Integer requestFormAnswerNo, @RequestBody ReplyAddDto replyAddDto) {
        return requestService.putRequestFormReply(requestFormAnswerNo, replyAddDto);
    }

    @PostMapping("/request/reply/report/{requestFormAnswerNo}/{targetNo}")
    @ResponseMessage("댓글 작성자 신고 추가 성공")
    @Operation(summary = "댓글 작성자 신고", description = "댓글 작성자를 신고한다.")
    @Parameters({
        @Parameter(name = "targetNo", description = "대상 의뢰인", example = "", schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "requestFormNo", description = "요청서 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    })
    public int postReportReplyForm(@PathVariable("requestFormAnswerNo") Integer requestFormAnswerNo, @PathVariable("targetNo") Integer targetNo, @RequestBody ReportDto reportDto) {
        return requestService.postReportReplyForm(requestFormAnswerNo, targetNo, reportDto);
    }

    @DeleteMapping("/request/form/reply/{requestFormAnswerNo}")
    @ResponseMessage("요청서 댓글 삭제 성공")
    @Operation(summary = "요청서 댓글 삭제", description = "요청서 댓글을 삭제한다.")
    @Parameter(name = "requestFormAnswerNo", description = "댓글 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public int deleteRequestFormReply(@PathVariable(required = false) Integer requestFormAnswerNo) {
        return requestService.deleteRequestFormReply(requestFormAnswerNo);
    }

    @DeleteMapping("/request/form/{requestFormNoArr}")
    @ResponseMessage("요청서 삭제 성공")
    @Operation(summary = "요청서 삭제", description = "요청서를 삭제한다.")
    @Parameter(name = "requestFormNoArr", description = "요청서 NO 콤마로 구분", example = "", schema = @Schema(type = "string"))
    public int deleteRequestForm(@PathVariable(required = false) String requestFormNoArr) {
        return requestService.deleteRequestForm(requestFormNoArr);
    }

    @GetMapping("/request/form/target")
    @ResponseMessage("지정 요청서 결제금액 조회 성공")
    @Operation(summary = "지정 요청서 결제금액 조회", description = "지정 요청서 결제금액을 조회한다.")
    @Parameters({
            @Parameter(name = "targetNo", description = "치자이나 NO", example = "", schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "requestDocGroupsNo", description = "의뢰서 그룹 no (콤마로 구분)", example = "", schema = @Schema(type = "string"))
    })
    public int getRequestFormTargetAmount(@RequestParam Integer targetNo, String requestDocGroupsNo) {
        return requestService.getRequestFormTargetAmount(targetNo, requestDocGroupsNo);
    }

    @GetMapping("/request/form/reply-list/{requestFormNo}")
    @ResponseMessage("요청서 댓글 목록 조회 성공")
    @Operation(summary = "요청서 댓글 목록 조회", description = "요청서 댓글 목록을 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public List<ReplyVo> getRequestFormReplyList(@PathVariable(required = false) Integer requestFormNo) {
        return requestService.getRequestFormReplyList(requestFormNo);
    }

    @PostMapping("/request/estimate/{requestFormNo}")
    @ResponseMessage("공개요청 견적서 작성 성공")
    @Operation(summary = "공개요청 견적서 작성", description = "공개요청 견적서를 작성한다.")
    @Parameters({
            @Parameter(name = "requestFormNo", description = "요청서 NO", example = "", schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "estimateCn", description = "견적서 내용", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "estimateAmount", description = "견적 금액", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "estimateDate", description = "견적 만료 날짜", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "estimateTime", description = "견적 만료 시간", example = "", schema = @Schema(type = "string"))
    })
    public int postRequestEstimate(@PathVariable("requestFormNo") Integer requestFormNo, @RequestBody EstimateAddDto estimateAddDto) throws Exception {
        return requestService.postRequestEstimate(requestFormNo, estimateAddDto);
    }

    @DeleteMapping("/request/doc/{requestDocGroupArr}")
    @ResponseMessage("의뢰서 삭제 성공")
    @Operation(summary = "의뢰서 삭제", description = "의뢰서를 삭제한다.")
    @Parameter(name = "requestDocGroupArr", description = "의뢰서 그룹 NO", example = "", schema = @Schema(type = "string"))
    public int deleteRequestDoc(@PathVariable(required = false) String requestDocGroupArr) {
        return requestService.deleteRequestDoc(requestDocGroupArr);
    }

    @GetMapping("/request/estimate/{requestFormNo}")
    @ResponseMessage("견적서 상세 조회 성공")
    @Operation(summary = "견적서 상세 조회", description = "견적서 상세를 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public RequestEstimateDetailVo getRequestEstimate(@PathVariable(required = false) Integer requestFormNo) {
        return requestService.getRequestEstimate(requestFormNo);
    }

    @GetMapping("/request/estimate/status/{requestFormNo}")
    @ResponseMessage("견적서 작성 여부 조회 성공")
    @Operation(summary = "견적서 작성 여부 조회", description = "견적서 작성 여부를 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public int getRequestEstimateStatus(@PathVariable(required = false) Integer requestFormNo) {
        return requestService.getRequestEstimateStatus(requestFormNo);
    }

    @PostMapping("/request/target-form/agree/{requestFormNo}")
    @ResponseMessage("지정 요청 수락 성공")
    @Operation(summary = "지정 요청 수락", description = "지정 요청을 수락한다.")
    @Parameters({
            @Parameter(name = "requestFormNo", description = "요청서 NO", example = "", schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "requestRefuseCn", description = "요청 수락 사유", example = "", schema = @Schema(type = "string"))
    })
    public int postRequestTargetFormAgree(@PathVariable(required = false) Integer requestFormNo) {
        return requestService.postRequestTargetFormAgree(requestFormNo);
    }

    @PostMapping("/request/target-form/refuse/{requestFormNo}")
    @ResponseMessage("지정 요청 거절 성공")
    @Operation(summary = "지정 요청 거절", description = "지정 요청을 거절한다.")
    @Parameters({
            @Parameter(name = "requestFormNo", description = "요청서 NO", example = "", schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "requestRefuseCn", description = "요청 거절 사유", example = "", schema = @Schema(type = "string"))
    })
    public int postRequestTargetFormRefuse(@PathVariable(required = false) Integer requestFormNo, @RequestBody RequestFormRefuseDto requestFormRefuseDto) {
        return requestService.postRequestTargetFormRefuse(requestFormNo, requestFormRefuseDto);
    }

    @GetMapping("/request/json/{requestDocGroupNo}")
    @ResponseMessage("의뢰서 수정용 데이터 조회 성공")
    @Operation(summary = "의뢰서 수정용 데이터 조회", description = "의뢰서 수정용 데이터를 조회한다.")
    @Parameter(name = "requestDocGroupNo", description = "의뢰서 그룹 NO", example = "", schema = @Schema(type = "string"))
    public RequestJsonVo getRequestJson(@PathVariable(required = false) Integer requestDocGroupNo) {
        return requestService.getRequestJson(requestDocGroupNo);
    }

    @GetMapping("/request/profile/{memberNo}")
    @ResponseMessage("프로필 데이터 조회 성공")
    @Operation(summary = "프로필 데이터 조회", description = "프로필 데이터를 조회한다.")
    @Parameter(name = "memberNo", description = "회원 NO", example = "", schema = @Schema(type = "string"))
    public ProfileVo getRequestProfile(@PathVariable(required = false) Integer memberNo) {
        return requestService.getRequestProfile(memberNo);
    }

    @GetMapping("/request/json/view/{requestDocGroupNo}/{requestFormNo}")
    @ResponseMessage("의뢰서 뷰용 데이터 조회 성공")
    @Operation(summary = "의뢰서 뷰용 데이터 조회", description = "의뢰서 수정용 데이터를 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "", schema = @Schema(type = "string"))
    @Parameter(name = "requestDocGroupNo", description = "의뢰서 그룹 NO", example = "", schema = @Schema(type = "string"))
    public RequestJsonVo getRequestJsonView(@PathVariable(required = false) Integer requestDocGroupNo, @PathVariable(required = false) Integer requestFormNo) {
        return requestService.getRequestJsonView(requestDocGroupNo, requestFormNo);
    }
}
