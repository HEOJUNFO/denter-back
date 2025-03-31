package com.dentner.front.api.mileage;


import com.dentner.core.cmmn.dto.MileageAddDto;
import com.dentner.core.cmmn.dto.MileageCalculateAddDto;
import com.dentner.core.cmmn.dto.MileageDto;
import com.dentner.core.cmmn.dto.MileageRefundAddDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.CardVo;
import com.dentner.core.cmmn.vo.MileageDesignerCalculateListVo;
import com.dentner.core.cmmn.vo.MileageDesignerListVo;
import com.dentner.core.cmmn.vo.MileageListVo;
import com.dentner.front.version.V1ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "공통 api", description = "공통 API")
public class MileageController implements V1ApiVersion {
	@Resource(name= "mileageService")
    MileageService mileageService;

    @GetMapping("/mileage/card")
    @ResponseMessage("카드정보 조회 성공")
    @Operation(summary = "카드정보", description = "카드정보를 조회한다.")
    public CardVo getCard() {
        return mileageService.getCard();
    }

    @PostMapping("/mileage/card")
    @ResponseMessage("카드 등록 성공")
    @Operation(summary = "카드 등록", description = "카드를 등록한다.")
    @Parameters({
            @Parameter(name = "cardCompanyNo", description = "카드사 NO", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "cardNumber", description = "카드 번호", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "cardCvc", description = "CVC 번호", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "cardMonth", description = "유효기간 월", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "cardDay", description = "유효기간 일", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "cardPassword", description = "카드 비밀번호", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "cardName", description = "카드 이름", example = "" ,  schema = @Schema(type = "string"))
    })
    public int postCard(@RequestBody String str) throws Exception{
        return mileageService.postCard(str);
    }

    @PutMapping("/mileage/card")
    @ResponseMessage("카드 변경 성공")
    @Operation(summary = "카드 변경", description = "카드를 변경한다.")
    @Parameters({
            @Parameter(name = "cardCompanyNo", description = "카드사 NO", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "cardNumber", description = "카드 번호", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "cardCvc", description = "CVC 번호", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "cardMonth", description = "유효기간 월", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "cardDay", description = "유효기간 일", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "cardPassword", description = "카드 비밀번호", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "cardName", description = "카드 이름", example = "" ,  schema = @Schema(type = "string"))
    })
    public int putCard(@RequestBody String str) throws Exception{
        return mileageService.putCard(str);
    }

    @PostMapping("/mileage/charge")
    @ResponseMessage("마일리지 충전 성공")
    @Operation(summary = "마일리지 충전", description = "마일리지를 충전한다.")
    @Parameters({
            @Parameter(name = "cardNo", description = "결제카드 NO", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "mileageSe", description = "마일리지 구분 (A:충전, B:결제, C:충전환불, D:결제환불)", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "mileageAmount", description = "마일리지 금액", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "mileageUnit", description = "마일리지 단위(A:원화, B:달러)", example = "" ,  schema = @Schema(type = "string"))
    })
    public int postMileageCharge(@RequestBody MileageAddDto cardAddDto) throws Exception{
        return mileageService.postMileageCharge(cardAddDto);
    }

    @GetMapping("/mileage/charge")
    @ResponseMessage("마일리지 충전 내역 성공")
    @Operation(summary = "마일리지 충전 내역 조회", description = "마일리지 충전 내역을 조회한다.")
    @Parameters({
            @Parameter(name = "startRow", description = "페이징 시작번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "pageCnt", description = "페이징 카운트", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "statusFilter", description = "상태 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색 시작 일자 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색 종료 일자 필터", example = "" ,  schema = @Schema(type = "string"))
    })
    public MileageListVo getMileageCharge(@ModelAttribute MileageDto mileageDto) throws Exception{
        return mileageService.getMileageCharge(mileageDto);
    }

    @GetMapping("/mileage/payment")
    @ResponseMessage("마일리지 결제 내역 성공")
    @Operation(summary = "마일리지 결제 내역 조회", description = "마일리지 결제 내역을 조회한다.")
    @Parameters({
            @Parameter(name = "startRow", description = "페이징 시작번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "pageCnt", description = "페이징 카운트", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "statusFilter", description = "상태 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색 시작 일자 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색 종료 일자 필터", example = "" ,  schema = @Schema(type = "string"))
    })
    public MileageListVo getMileagePayment(@ModelAttribute MileageDto mileageDto) {
        return mileageService.getMileagePayment(mileageDto);
    }

    @GetMapping("/mileage")
    @ResponseMessage("보유 마일리지 조회 성공")
    @Operation(summary = "보유 마일리지 정보", description = "보유 마일리지 정보를 조회한다.")
    public int getMileage() {
        return mileageService.getMileage();
    }

    @GetMapping("/mileage/designer")
    @ResponseMessage("치자이너 보유 마일리지 조회 성공")
    @Operation(summary = "치자이너 보유 마일리지 정보", description = "보유 마일리지 정보를 조회한다.")
    public Map<String, Object> getMileageDesigner() {
        return mileageService.getMileageDesigner();
    }

    @GetMapping("/mileage/designer-calculate")
    @ResponseMessage("치자이너 정산 마일리지 조회 성공")
    @Operation(summary = "치자이너 정산 마일리지 정보", description = "정산 마일리지 정보를 조회한다.")
    public Map<String, Object> getMileageDesignerCalculate() {
        return mileageService.getMileageDesignerCalculate();
    }

    @PostMapping("/mileage/refund")
    @ResponseMessage("마일리지 환불 요청 성공")
    @Operation(summary = "마일리지 환불 요청", description = "마일리지 환불을 요청한다.")
    @Parameters({
            @Parameter(name = "mileageNo", description = "마일리지 NO", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "mileageRefundCodeNo", description = "마일리지 환불 사유 no", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "mileageRefundSe", description = "마일리지 구분(A:충전,B:결제)", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "mileageRefundCn", description = "마일리지 환불 직접 입력", example = "" ,  schema = @Schema(type = "string"))
    })
    public int postMileageRefund(@RequestBody MileageRefundAddDto mileageRefundAddDto) throws Exception{
        return mileageService.postMileageRefund(mileageRefundAddDto);
    }

    @PostMapping("/mileage/calculate")
    @ResponseMessage("마일리지 정산 요청 성공")
    @Operation(summary = "마일리지 정산 요청", description = "마일리지 정산을 요청한다.")
    @Parameters({
            @Parameter(name = "mileageNo", description = "마일리지 NO", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "calculateAmount", description = "정산 금액", example = "" ,  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "calculateSe", description = "정산 구분(A:전체정산,B:입금정산)", example = "" ,  schema = @Schema(type = "string"))
    })
    public int postMileageCalculate(@RequestBody MileageCalculateAddDto mileageCalculateAddDto) throws Exception{
        return mileageService.postMileageCalculate(mileageCalculateAddDto);
    }

    @GetMapping("/mileage/designer/deposit")
    @ResponseMessage("치자이너 마일리지 입금 내역 조회 성공")
    @Operation(summary = "치자이너 마일리지 입금 내역 조회", description = "치자이너 마일리지 입금 내역을 조회한다.")
    @Parameters({
            @Parameter(name = "startRow", description = "페이징 시작번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "pageCnt", description = "페이징 카운트", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "amountFilter", description = "금액 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색 시작 일자 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색 종료 일자 필터", example = "" ,  schema = @Schema(type = "string"))
    })
    public MileageDesignerListVo getMileageDeposit(@ModelAttribute MileageDto mileageDto) {
        return mileageService.getMileageDeposit(mileageDto);
    }

    @GetMapping("/mileage/designer/calculate")
    @ResponseMessage("마일리지 정산 내역 조회 성공")
    @Operation(summary = "마일리지 정산 내역 조회", description = "마일리지 정산 내역을 조회한다.")
    @Parameters({
            @Parameter(name = "startRow", description = "페이징 시작번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "pageCnt", description = "페이징 카운트", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "statusFilter", description = "상태 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색 시작 일자 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색 종료 일자 필터", example = "" ,  schema = @Schema(type = "string"))
    })
    public MileageDesignerCalculateListVo getMileageCalculate(@ModelAttribute MileageDto mileageDto) {
        return mileageService.getMileageCalculate(mileageDto);
    }

    @PostMapping("/mileage/easy/{amount}")
    @ResponseMessage("간편결제 전 결제 요청 성공")
    @Operation(summary = "간편결제 전 결제 요청", description = "간편결제 전 결제정보 요청한다.")
    @Parameter(name = "amount", description = "결제금액", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public HashMap<String, Object> getEasyPay(@PathVariable(required = false) Integer amount) throws Exception{
        return mileageService.getEasyPay(amount);
    }

    @PostMapping("/mileage/easy-success/{unit}")
    @ResponseMessage("간편결제 요청 성공")
    @Operation(summary = "간편결제 요청", description = "간편결제 요청한다.")
    @Parameter(name = "unit", description = "단위", example = "",  schema = @Schema(type = "string"))
    public int postEasy(@RequestBody String str, @PathVariable(required = false) String unit) throws Exception{
        return mileageService.postEasy(str, unit);
    }

    @PostMapping("/mileage/paypal/{amount}")
    @ResponseMessage("페이팔 전 결제 요청 성공")
    @Operation(summary = "페이팔 전 결제 요청", description = "페이팔 전 결제정보 요청한다.")
    @Parameter(name = "amount", description = "결제금액", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public HashMap<String, Object> getPaypal(@PathVariable(required = false) Integer amount) throws Exception{
        return mileageService.getPaypal(amount);
    }

    @PostMapping("/mileage/paypal")
    @ResponseMessage("페이팔 결제 요청 성공")
    @Operation(summary = "페이팔 결제 요청", description = "페이팔 결제를 요청한다.")
    @Parameter(name = "str", description = "결제정보", example = "",  schema = @Schema(type = "string"))
    public int postPaypal(@RequestBody String str) throws Exception{
        return mileageService.postPaypal(str);
    }
}
