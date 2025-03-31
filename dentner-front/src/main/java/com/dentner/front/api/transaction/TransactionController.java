package com.dentner.front.api.transaction;


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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "거래 api", description = "거래 API")
public class TransactionController implements V1ApiVersion {
	@Resource(name= "transactionService")
    TransactionService transactionService;

    @GetMapping({"/transaction/{requestFormSe}", "/transaction"})
    @ResponseMessage("거래내역 목록 성공")
    @Operation(summary = "거래내역 목록 조회", description = "거래내역 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "startRow", description = "페이징 시작번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "pageCnt", description = "페이징 카운트", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "statusFilter", description = "상태 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색 시작 일자 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색 종료 일자 필터", example = "" ,  schema = @Schema(type = "string"))
    })
    public RequestFormListVo getTransactionFormList(HttpServletRequest request, @PathVariable(required = false) String requestFormSe, @ModelAttribute RequestFormDto requestFormDto) {
        return transactionService.getTransactionFormList(request, requestFormSe, requestFormDto);
    }

    @GetMapping("/transaction/estimate/{requestFormNo}/{estimateSe}")
    @ResponseMessage("요청서 견적 목록 성공")
    @Operation(summary = "요청서 견적 목록 조회", description = "요청서 견적 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "requestFormNo", description = "요청서 NO", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "estimateSe", description = "견적 구분", example = "" ,  schema = @Schema(type = "string"))
    })
    public RequestEstimateListVo getTransactionEstimateList(@PathVariable(required = false) Integer requestFormNo,
                                                            @PathVariable(required = false) String estimateSe,
                                                            @ModelAttribute RequestEstimateDto requestEstimateDto) {
        return transactionService.getTransactionEstimateList(requestFormNo, estimateSe, requestEstimateDto);
    }

    @PostMapping("/transaction/estimate/choice/{requestFormNo}/{targetNo}")
    @ResponseMessage("치자이너 선택 성공")
    @Operation(summary = "치자이너 선택", description = "치자이너를 선택한다.")
    @Parameters({
        @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "targetNo", description = "치자이너 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    })
    public int postEstimateChoice(@PathVariable("requestFormNo") Integer requestFormNo, @PathVariable("targetNo") Integer memberNo){
        return transactionService.postEstimateChoice(requestFormNo, memberNo);
    }

    @PutMapping("/transaction/state/{requestFormNo}/{status}")
    @ResponseMessage("요청서 진행상태 변경 성공")
    @Operation(summary = "요청서 진행상태 변경", description = "요청서 진행상태를 변경한다.")
    @Parameters({
        @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "status", description = "진행상태", example = "",  schema = @Schema(type = "string"))
    })
    public int putRequestStatus(@PathVariable("requestFormNo") Integer requestFormNo,
                                @PathVariable("status") String status){
        return transactionService.putRequestStatus(requestFormNo, status);
    }

    @PutMapping("/transaction/deal/{requestFormNo}/{status}")
    @ResponseMessage("요청서 거래상태 변경 성공")
    @Operation(summary = "요청서 거래상태 변경", description = "요청서 거래상태를 변경한다.")
    @Parameters({
        @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "status", description = "거래상태", example = "",  schema = @Schema(type = "string"))
    })
    public int putRequestDealStatus(@PathVariable("requestFormNo") Integer requestFormNo,
                                    @PathVariable("status") String status){
        return transactionService.putRequestDealStatus(requestFormNo, status);
    }

    @GetMapping("/transaction/payment/{requestFormNo}")
    @ResponseMessage("의뢰서 결제 조회 성공")
    @Operation(summary = "의뢰서 결제 조회", description = "의뢰서 결제를 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public TransactionPaymentVo getTransactionPayment(@PathVariable(required = false) Integer requestFormNo) {
        return transactionService.getTransactionPayment(requestFormNo);
    }

    @GetMapping("/transaction/doc/{requestFormNo}")
    @ResponseMessage("의뢰서 정보 조회 성공")
    @Operation(summary = "의뢰서 정보 조회", description = "의뢰서 정보를 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public List<Map<String, Object>> getTransactionDoc(@PathVariable(required = false) Integer requestFormNo) {
        return transactionService.getTransactionDoc(requestFormNo);
    }

    @GetMapping("/transaction/doc/detail/{requestDocGroupNo}")
    @ResponseMessage("의뢰서 상세 조회 성공")
    @Operation(summary = "의뢰서 상세 조회", description = "의뢰서 상세를 조회한다.")
    @Parameter(name = "requestDocGroupNo", description = "의뢰서 그룹 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public List<RequestDocVo> getTransactionDocDetail(@PathVariable(required = false) Integer requestDocGroupNo) {
        return transactionService.getTransactionDocDetail(requestDocGroupNo);
    }

    @PostMapping("/transaction/cad/{requestFormNo}")
    @ResponseMessage("CAD파일 업로드 성공")
    @Operation(summary = "CAD파일 업로드", description = "CAD파일을 업로드한다.")
    @Parameters({
        @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "requestPaySe", description = "추가금 여부", example = "",  schema = @Schema(type = "string")),
        @Parameter(name = "requestPayUnit", description = "단위", example = "",  schema = @Schema(type = "string")),
        @Parameter(name = "requestPayAmount", description = "금액", example = "",  schema = @Schema(type = "string")),
        @Parameter(name = "requestPayCn", description = "요청사유", example = "",  schema = @Schema(type = "string")),
        @Parameter(name = "cadFiles", description = "요청사유", example = "",  schema = @Schema(type = "string")),
        @Parameter(name = "files", description = "요청사유", example = "",  schema = @Schema(type = "string")),
        @Parameter(name = "docList", description = "요청서 파일 list", example = "",  schema = @Schema(type = "string"))
    })
    public int postTransactionCad(@PathVariable(required = false) Integer requestFormNo,
                                    @ModelAttribute TransactionCadAddDto transactionCadAddDto,
                                    @RequestParam(required = false) List<MultipartFile> cadFiles,
                                    @RequestParam(required = false) List<MultipartFile> files) throws Exception{
        return transactionService.postTransactionCad(requestFormNo, transactionCadAddDto, cadFiles, files);
    }

    @GetMapping("/transaction/estimate/detail/{requestEstimateNo}")
    @ResponseMessage("의뢰인 본인 견적서 상세 조회 성공")
    @Operation(summary = "의뢰인 본인 견적서 상세 조회", description = "의뢰서 본인이 치자이너가 작성한 견적서 상세를 조회한다.")
    @Parameters({
            @Parameter(name = "requestEstimateNo", description = "견적서 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    })
    public TransactionEstimateDetailVo getTransactionEstimate(@PathVariable(required = false) Integer requestEstimateNo) {
        return transactionService.getTransactionEstimate(requestEstimateNo);
    }

    @GetMapping("/transaction/estimate/detail/designer/{requestFormNo}")
    @ResponseMessage("치자이너 견적서 상세 조회 성공")
    @Operation(summary = "치자이너 본인 견적서 상세 조회", description = "치자이너 본인이 작성한 견적서 상세를 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public TransactionEstimateDetailVo getTransactionEstimateDesigner(@PathVariable(required = false) Integer requestFormNo) {
        return transactionService.getTransactionEstimateDesigner(requestFormNo);
    }

    @PostMapping("/transaction/pay/{requestFormNo}")
    @ResponseMessage("요청서 결제 성공")
    @Operation(summary = "요청서 결제", description = "요청서를 결제한다.")
    @Parameters({
        @Parameter(name = "requestFormNo", description = "견적서 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    })
    public int postTransactionPay(@PathVariable("requestFormNo") Integer requestFormNo, @RequestBody MileageAddDto mileageAddDto){
        return transactionService.postTransactionPay(requestFormNo, mileageAddDto);
    }

    @GetMapping("/transaction/contract/{requestFormNo}")
    @ResponseMessage("계약 정보 조회 성공")
    @Operation(summary = "계약 정보 조회", description = "계약 정보를 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public Map<String, Object> getTransactionContract(@PathVariable(required = false) Integer requestFormNo) {
        return transactionService.getTransactionContract(requestFormNo);
    }

    @PutMapping("/transaction/contract/{requestFormNo}")
    @ResponseMessage("의뢰서 계약 선택 성공")
    @Operation(summary = "의뢰서 계약 선택", description = "의뢰서 계약을 진행할지 선택한다.")
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int putTransactionContract(@PathVariable("requestFormNo") Integer requestFormNo, @RequestBody RequestFormContractDto requestFormContractDto){
        return transactionService.putTransactionContract(requestFormNo, requestFormContractDto);
    }

    @PutMapping("/transaction/viewer3d/{requestFormNo}/{viewerSe}/{requestFormSe}")
    @ResponseMessage("의뢰서 3D뷰어 소통여부 선택 성공")
    @Operation(summary = "의뢰서 3D뷰어 소통여부 선택", description = "의뢰서 3D뷰어 소통여부를 진행할지 선택한다.")
    @Parameters({
        @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "viewerSe", description = "선택 여부", example = "",  schema = @Schema(type = "string")),
        @Parameter(name = "requestFormSe", description = "공개 지정 여부", example = "",  schema = @Schema(type = "string"))
    })
    public int putTransactionViewer3d(@PathVariable("requestFormNo") Integer requestFormNo,
                                      @PathVariable("viewerSe") String viewerSe,
                                      @PathVariable("requestFormSe") String requestFormSe){
        return transactionService.putTransactionViewer3d(requestFormNo, viewerSe, requestFormSe);
    }

    @PutMapping("/transaction/pay/{requestFormPayNo}")
    @ResponseMessage("추가금 수정 성공")
    @Operation(summary = "추가금 수정", description = "추가금을 수정한다.")
    @Parameter(name = "requestFormPayNo", description = "추가금 시퀀스 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int putTransactionPay(@PathVariable(required = false) Integer requestFormPayNo,
                                  @ModelAttribute TransactionCadAddDto transactionCadAddDto,
                                  @RequestParam(required = false) List<MultipartFile> files) throws Exception{
        return transactionService.putTransactionPay(requestFormPayNo, transactionCadAddDto, files);
    }

    @PutMapping("/transaction/cad/{requestFormNo}")
    @ResponseMessage("CAD파일 수정 성공")
    @Operation(summary = "CAD파일 수정", description = "CAD파일을 수정한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int putTransactionCad(@PathVariable(required = false) Integer requestFormNo,
                                  @ModelAttribute TransactionCadAddDto transactionCadAddDto,
                                  @RequestParam(required = false) List<MultipartFile> cadFiles) throws Exception{
        return transactionService.putTransactionCad(requestFormNo, transactionCadAddDto, cadFiles);
    }

    @PutMapping("/transaction/doc/receive/{requestFormNo}/{requestFormSe}")
    @ResponseMessage("의뢰서 수령 성공")
    @Operation(summary = "의뢰서 수령", description = "의뢰서를 수령한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    @Parameter(name = "type", description = "타입 (A:공개요청, B:지정요청)", example = "",  schema = @Schema(type = "string"))
    public int putTransactionDocReceive(@PathVariable(required = false) Integer requestFormNo,
                                        @PathVariable(required = false) String requestFormSe) throws Exception{
        return transactionService.putTransactionDocReceive(requestFormNo, requestFormSe);
    }

    @GetMapping("/transaction/add-pay/{requestFormNo}/{memberSe}")
    @ResponseMessage("추가금 정보 조회 성공")
    @Operation(summary = "추가금 정보 조회", description = "추가금 정보를 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public RequestAddPayVo getTransactionAddPay(@PathVariable(required = false) Integer requestFormNo,
                                                @PathVariable(required = false) String memberSe) {
        return transactionService.getTransactionAddPay(requestFormNo, memberSe);
    }

    @PostMapping("/transaction/add-pay/{requestFormNo}")
    @ResponseMessage("추가금 결제 성공")
    @Operation(summary = "추가금 결제", description = "추가금을 결제한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int postTransactionAddPay(@PathVariable("requestFormNo") Integer requestFormNo, @RequestBody MileageAddDto mileageAddDto){
        return transactionService.postTransactionAddPay(requestFormNo, mileageAddDto);
    }

    @PostMapping("/transaction/remaking/{requestFormNo}")
    @ResponseMessage("재제작 요청 성공")
    @Operation(summary = "재제작 요청", description = "재제작을 요청한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int postTransactionRemaking(@PathVariable("requestFormNo") Integer requestFormNo,
                                       @RequestParam(required = false) List<MultipartFile> files,
                                       @ModelAttribute RequestRemakingAddDto requestRemakingAddDto){
        return transactionService.postTransactionRemaking(requestFormNo, files, requestRemakingAddDto);
    }

    @GetMapping("/transaction/remaking/{requestFormNo}")
    @ResponseMessage("재제작 요청내역 조회 성공")
    @Operation(summary = "재제작 요청내역 조회", description = "재제작 요청내역을 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public RequestRemakingVo getTransactionRemaking(@PathVariable(required = false) Integer requestFormNo) {
        return transactionService.getTransactionRemaking(requestFormNo);
    }

    @GetMapping("/transaction/cad/{requestFormNo}/{memberSe}")
    @ResponseMessage("cad 파일 정보 조회 성공")
    @Operation(summary = "cad 파일  조회", description = "cad 파일을 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    @Parameter(name = "memberSe", description = "회원 구분", example = "",  schema = @Schema(type = "string"))
    public List<RequestCadFileVo> getTransactionCadFile(@PathVariable(required = false) Integer requestFormNo,
                                                        @PathVariable(required = false) String memberSe) {
        return transactionService.getTransactionCadFile(requestFormNo, memberSe);
    }

    @PostMapping("/transaction/review/{targetNo}/{requestFormNo}")
    @ResponseMessage("리뷰 작성 성공")
    @Operation(summary = "리뷰작성", description = "리뷰를 작성 한다.")
    @Parameters({
            @Parameter(name = "targetNo", description = "대상 치자이너", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "requestFormNo", description = "요청서 NO", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "reviewRate", description = "리뷰 별점", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "reviewCn", description = "리뷰 내용", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "images", description = "리뷰 사진", example = "",  schema = @Schema(type = "string"))
    })
    public int postReview(@PathVariable("targetNo") Integer targetNo,
                          @PathVariable("requestFormNo") Integer requestFormNo,
                          @RequestParam(required = false) List<MultipartFile> images,
                          @ModelAttribute ReviewDto reviewDto){
        return transactionService.postReview(targetNo, requestFormNo, images, reviewDto);
    }

    @GetMapping("/transaction/mileage")
    @ResponseMessage("마일리지 조회 성공")
    @Operation(summary = "마일리지 조회", description = "마일리지를 조회한다.")
    public Integer getTransactionMileage() {
        return transactionService.getTransactionMileage();
    }

    @DeleteMapping("/transaction/add-pay/{requestFormNo}")
    @ResponseMessage("추가금 철회 성공")
    @Operation(summary = "추가금 철회", description = "추가금을 철회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int deleteTransactionAddPay(@PathVariable("requestFormNo") Integer requestFormNo){
        return transactionService.deleteTransactionAddPay(requestFormNo);
    }

    @DeleteMapping("/transaction/remaking/{requestFormNo}")
    @ResponseMessage("재제작 철회 성공")
    @Operation(summary = "재제작 철회", description = "재제작을 철회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int deleteTransactionRemaking(@PathVariable("requestFormNo") Integer requestFormNo){
        return transactionService.deleteTransactionRemaking(requestFormNo);
    }

    @GetMapping("/transaction/stat")
    @ResponseMessage("거래내역 현황 조회 성공")
    @Operation(summary = "거래내역 현황 조회 조회", description = "거래내역 현황을 조회한다.")
    public TransactionStatVo getTransactionStat() {
        return transactionService.getTransactionStat();
    }

    @PostMapping("/transaction/approval/{requestFormNo}")
    @ResponseMessage("전자결재 전송 성공")
    @Operation(summary = "전자결재 전송", description = "전자결재를 전송 한다.")
    public int postKtApproval(@PathVariable("requestFormNo") Integer requestFormNo) throws Exception{
        return transactionService.postKtApproval(requestFormNo);
    }

    @PutMapping("/transaction/cancel/{requestFormNo}")
    @ResponseMessage("거래취소 성공")
    @Operation(summary = "거래취소 성공", description = "거래를 취소한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int updateTransactionCancel(@PathVariable("requestFormNo") Integer requestFormNo,
                                       @RequestBody RequestFormCancelDto requestFormCancelDto) throws Exception{
        return transactionService.updateTransactionCancel(requestFormNo, requestFormCancelDto);
    }

    @GetMapping("/transaction/status/{requestFormNo}")
    @ResponseMessage("요청서 현재 상태 조회 성공")
    @Operation(summary = "요청서 현재 상태 조회", description = "요청서 현재 상태를 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public TransactionStatusVo getTransactionStatus(@PathVariable(required = false) Integer requestFormNo) {
        return transactionService.getTransactionStatus(requestFormNo);
    }

    @GetMapping("/transaction/data-transfer/{requestFormNo}")
    @ResponseMessage("공전소 전송여부 조회 성공")
    @Operation(summary = "공전소 전송여부 조회", description = "공전소 전송여부를 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public TransactionDataVo getTransactionData(@PathVariable(required = false) Integer requestFormNo) {
        return transactionService.getTransactionData(requestFormNo);
    }

    @PutMapping("/transaction/cancel/confirm/{requestFormNo}")
    @ResponseMessage("거래취소 승인 성공")
    @Operation(summary = "거래취소 승인 성공", description = "거래취소를 승인한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int updateTransactionCancelConfirm(@PathVariable("requestFormNo") Integer requestFormNo){
        return transactionService.updateTransactionCancelConfirm(requestFormNo);
    }

    @DeleteMapping("/transaction/history/{requestFormNo}")
    @ResponseMessage("거래내역 삭제 성공")
    @Operation(summary = "거래내역 삭제", description = "거래내역 삭제한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int deleteTransactionHistory(@PathVariable("requestFormNo") Integer requestFormNo){
        return transactionService.deleteTransactionHistory(requestFormNo);
    }

    @PutMapping("/transaction/cancel/reject/{requestFormNo}")
    @ResponseMessage("거래취소 거절 성공")
    @Operation(summary = "거래취소 거절 성공", description = "거래취소를 거절한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int updateTransactionCancelReject(@PathVariable("requestFormNo") Integer requestFormNo){
        return transactionService.updateTransactionCancelReject(requestFormNo);
    }

    @PostMapping("/transaction/3d/{requestFormNo}")
    @ResponseMessage("3d 정보 저장 성공")
    @Operation(summary = "3d 정보 저장", description = "3d 정보를 저장한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int postTransaction3dInfo(@PathVariable("requestFormNo") Integer requestFormNo,
                                       @RequestParam(required = false) List<MultipartFile> files,
                                       @ModelAttribute Request3dInfoAdd request3dInfoAdd){
        return transactionService.postTransaction3dInfo(requestFormNo, files, request3dInfoAdd);
    }

    @GetMapping("/transaction/3d/{requestFormNo}")
    @ResponseMessage("3d 정보 조회 성공")
    @Operation(summary = "3d 정보 조회", description = "3d 정보를 조회한다.")
    @Parameter(name = "requestFormNo", description = "요청서 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public List<ThreeInfoVO> getTransaction3dInfo(@PathVariable(required = false) Integer requestFormNo) {
        return transactionService.getTransaction3dInfo(requestFormNo);
    }

    @DeleteMapping("/transaction/3d/{threeInfoNoArr}")
    @ResponseMessage("3d 정보 삭제성공")
    @Operation(summary = "3d 정보 삭제", description = "3d 정보를 삭제한다.")
    @Parameter(name = "threeInfoNo", description = "3d 번호", example = "",  schema = @Schema(type = "string"))
    public int deleteTransaction3dInfo(@PathVariable("threeInfoNoArr") String threeInfoNoArr){
        return transactionService.deleteTransaction3dInfo(threeInfoNoArr);
    }

    @GetMapping("/transaction/3d/memo/{threeFileNo}")
    @ResponseMessage("3d 메모 조회 성공")
    @Operation(summary = "3d 메모 조회", description = "3d 메모를 조회한다.")
    @Parameter(name = "threeFileNo", description = "3d 파일 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public List<ThreeMemoVO> getTransaction3dMemo(@PathVariable(required = false) Integer threeFileNo) {
        return transactionService.getTransaction3dMemo(threeFileNo);
    }

    @PostMapping("/transaction/3d/memo/{threeFileNo}")
    @ResponseMessage("3d 메모 저장 성공")
    @Operation(summary = "3d 메모 저장", description = "3d 메모를 저장한다.")
    @Parameter(name = "threeFileNo", description = "3d 파일 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public Request3dMemoAdd postTransaction3dMemo(@PathVariable("threeFileNo") Integer threeFileNo,
                                     @RequestBody Request3dMemoAdd request3dMemoAdd){
        return transactionService.postTransaction3dMemo(threeFileNo, request3dMemoAdd);
    }

    @DeleteMapping("/transaction/3d/memo/{threeMemoNoArr}")
    @ResponseMessage("3d 메모 삭제성공")
    @Operation(summary = "3d 메모 삭제", description = "3d 메모를 삭제한다.")
    @Parameter(name = "threeMemoNo", description = "3d 메모 번호", example = "",  schema = @Schema(type = "string"))
    public int deleteTransaction3dMemo(@PathVariable("threeMemoNoArr") String threeMemoNoArr){
        return transactionService.deleteTransaction3dMemo(threeMemoNoArr);
    }

    @PostMapping("/transaction/3d/alarm-talk/{requestFormNo}/{type}")
    @ResponseMessage("3d 알림톡 발송 성공")
    @Operation(summary = "3d 알림톡 발송", description = "3d 알림톡을 발송한다.")
    @Parameter(name = "requestFormNo", description = "요청서 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int postTransaction3dAlarmTalk(@PathVariable("requestFormNo") Integer requestFormNo,
                                          @PathVariable("type") String type) throws Exception{
        return transactionService.postTransaction3dAlarmTalk(requestFormNo, type);
    }
}
