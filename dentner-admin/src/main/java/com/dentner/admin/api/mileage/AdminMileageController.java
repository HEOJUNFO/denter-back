package com.dentner.admin.api.mileage;


import com.dentner.admin.version.V1ApiVersion;
import com.dentner.core.cmmn.dto.AdminMileageDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "관리자 마일리지 관리", description = "관리자 마일리지 API")
public class AdminMileageController implements V1ApiVersion {

	@Resource(name= "adminMileageService")
    AdminMileageService adminMileageService;

    @GetMapping("/mileage/charge")
    @ResponseMessage("충전/환불 목록 조회 성공")
    @Operation(summary = "충전/환불 목록", description = "충전/환불 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "searchType", description = "조회 타입", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "searchKeyword", description = "검색어 ID / 닉네임 / 이름 / 신고내용", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색시작일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색종료일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "statusFilter", description = "전체 / 비활성화 / 활성화 필터", example = "",  schema = @Schema(type = "string"))
    })
    public AdminChargeListVo getChargeList(@ModelAttribute AdminMileageDto adminMileageDto) {
        return adminMileageService.getChargeList(adminMileageDto);
    }

    @GetMapping("/mileage/charge/excel")
    public ResponseEntity<byte[]> getChargeExcel(@ModelAttribute AdminMileageDto adminMileageDto) throws IOException {
        // 엑셀 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("결제환불내역");

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = {"국가 타입", "충전한 마일리지", "결제금액", "결제수단","결제일", "결제자 이메일" , "결제자 닉네임", "결제자 이름", "환불 요청여부", "환불 사유", "환불 요청일", "환불 완료여부"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }
        // 데이터 추가
        AdminChargeListVo list = adminMileageService.getChargeList(adminMileageDto);
        List<AdminChargeVo> formList =  list.getList();

        int rowNum = 1;
        for (AdminChargeVo form : formList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(form.getMileageUnitName());
            row.createCell(1).setCellValue(form.getMileageAmount());
            row.createCell(2).setCellValue("Y".equals(form.getRefundYn()) ? "0원" : form.getMileageAmount()+"원");
            row.createCell(3).setCellValue(form.getPayType());
            row.createCell(4).setCellValue(form.getRegisterDt());
            row.createCell(5).setCellValue(form.getMemberEmail());
            row.createCell(6).setCellValue(form.getMemberNickName());
            row.createCell(7).setCellValue(form.getMemberName());
            row.createCell(8).setCellValue(form.getRefundYn());
            row.createCell(9).setCellValue(form.getMileageRefundCn());
            row.createCell(10).setCellValue(form.getRefundDt());
            row.createCell(11).setCellValue(form.getMileageStatusName());
        }

        // 엑셀 파일을 ByteArrayOutputStream으로 쓰기
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=charge_list.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

    @GetMapping("/mileage/pay")
    @ResponseMessage("결제/환불 목록 조회 성공")
    @Operation(summary = "결제/환불 목록", description = "결제/환불 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "searchKeyword", description = "검색어 ID / 닉네임 / 이름 / 신고내용", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색시작일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색종료일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "statusFilter", description = "전체 / 비활성화 / 활성화 필터", example = "",  schema = @Schema(type = "string"))
    })
    public AdminPayListVo getPayList(@ModelAttribute AdminMileageDto adminMileageDto) {
        return adminMileageService.getPayList(adminMileageDto);
    }

    @GetMapping("/mileage/pay/excel")
    public ResponseEntity<byte[]> getPayExcel(@ModelAttribute AdminMileageDto adminMileageDto) throws IOException {
        // 엑셀 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("결제환불입금내역");

        List<String> headerList = new ArrayList<String>();
        String searchType = adminMileageDto.getSearchType();

        if("A".equals(searchType)){
            headerList.add("국가타입");
            headerList.add("거래진행상태");
            headerList.add("요청서 명");
            headerList.add("결제일");
            headerList.add("결제한 마일리지");
            headerList.add("결제자 ID");
            headerList.add("결제자 닉네임");
            headerList.add("결제자 이름");
            headerList.add("치자이너 ID");
            headerList.add("치자이너 닉네임");
            headerList.add("치자이너 이름");
            headerList.add("cad 파일 다운로드여부");
            headerList.add("환불요청여부");
            headerList.add("환불사유");
            headerList.add("환불요청일");
            headerList.add("환불 완료여부");
            headerList.add("치자이너에게 입금완료여부");
            headerList.add("실 입금 마일리지");
            headerList.add("실 환불 마일리지");
            headerList.add("은행명");
            headerList.add("예금주");
            headerList.add("계좌번호");
        }else{
            headerList.add("국가타입");
            headerList.add("거래진행상태");
            headerList.add("요청서 명");
            headerList.add("결제한 마일리지");
            headerList.add("결제자 ID");
            headerList.add("결제자 닉네임");
            headerList.add("결제자 이름");
            headerList.add("치자이너 ID");
            headerList.add("치자이너 닉네임");
            headerList.add("치자이너 이름");
            headerList.add("환불요청여부");
            headerList.add("환불사유");
            headerList.add("환불요청일");
            headerList.add("실 환불 마일리지");
        }
        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = headerList.toArray(new String[0]);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }
        // 데이터 추가
        AdminPayListVo list = adminMileageService.getPayList(adminMileageDto);
        List<AdminPayVo> formList =  list.getList();

        int rowNum = 1;
        for (AdminPayVo form : formList) {
            Row row = sheet.createRow(rowNum++);
            if("A".equals(searchType)){
                row.createCell(0).setCellValue(form.getMileageUnitName());
                row.createCell(1).setCellValue(form.getRequestStatusName());
                row.createCell(2).setCellValue(form.getRequestFormSj());
                row.createCell(3).setCellValue(form.getRegisterDt());
                row.createCell(4).setCellValue(form.getMileageAmount());
                row.createCell(5).setCellValue(form.getMemberEmail());
                row.createCell(6).setCellValue(form.getMemberNickName());
                row.createCell(7).setCellValue(form.getMemberName());
                row.createCell(8).setCellValue(form.getDesignerEmail());
                row.createCell(9).setCellValue(form.getDesignerNickName());
                row.createCell(10).setCellValue(form.getDesignerName());
                row.createCell(11).setCellValue(form.getCadYn());
                row.createCell(12).setCellValue(form.getRefundYn());
                row.createCell(13).setCellValue(form.getMileageRefundCn());
                row.createCell(14).setCellValue(form.getRefundDt());
                row.createCell(15).setCellValue(form.getMileageStatus());
                row.createCell(16).setCellValue(form.getMileageStatus());
                row.createCell(17).setCellValue(form.getCalculateYn());
                row.createCell(18).setCellValue("");
                row.createCell(19).setCellValue(form.getMemberBankNoName());
                row.createCell(20).setCellValue(form.getMemberAccountName());
                row.createCell(21).setCellValue(form.getMemberAccountNumber());
            }else{
                row.createCell(0).setCellValue(form.getMileageUnitName());
                row.createCell(1).setCellValue(form.getRequestStatusName());
                row.createCell(2).setCellValue(form.getRequestFormSj());
                row.createCell(3).setCellValue(form.getRegisterDt());
                row.createCell(4).setCellValue(form.getMileageAmount());
                row.createCell(5).setCellValue(form.getMemberEmail());
                row.createCell(6).setCellValue(form.getMemberNickName());
                row.createCell(7).setCellValue(form.getMemberName());
                row.createCell(8).setCellValue(form.getDesignerEmail());
                row.createCell(9).setCellValue(form.getDesignerNickName());
                row.createCell(10).setCellValue(form.getDesignerName());
                row.createCell(11).setCellValue(form.getRefundYn());
                row.createCell(12).setCellValue(form.getMileageRefundCn());
                row.createCell(13).setCellValue(form.getRefundDt());
                row.createCell(14).setCellValue("");
            }
        }

        // 엑셀 파일을 ByteArrayOutputStream으로 쓰기
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=charge_list.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

    @GetMapping("/mileage/calculate")
    @ResponseMessage("정산 목록 조회 성공")
    @Operation(summary = "정산 목록", description = "정산 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "searchKeyword", description = "검색어 ID / 닉네임 / 이름 / 신고내용", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색시작일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색종료일자", example = "",  schema = @Schema(type = "string"))
    })
    public AdminCalculateListVo getCalculateList(@ModelAttribute AdminMileageDto adminMileageDto) {
        return adminMileageService.getCalculateList(adminMileageDto);
    }

    @GetMapping("/mileage/calculate/excel")
    public ResponseEntity<byte[]> getCalculateExcel(@ModelAttribute AdminMileageDto adminMileageDto) throws IOException {
        // 엑셀 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("정산내역");

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = {"정산요청 마일리지", "실 정산 금액", "치자이너 ID", "치자이너 닉네임","은행명", "예금주" , "계좌번호", "정산요청일", "계산서 발행여부", "정산 완료여부"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }
        // 데이터 추가
        AdminCalculateListVo list = adminMileageService.getCalculateList(adminMileageDto);
        List<AdminCalculateVo> formList =  list.getList();

        int rowNum = 1;
        for (AdminCalculateVo form : formList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(form.getMileageAmount());
            row.createCell(1).setCellValue(form.getCalculateAmount());
            row.createCell(2).setCellValue(form.getMemberEmail());
            row.createCell(3).setCellValue(form.getMemberNickName());
            row.createCell(4).setCellValue(form.getMemberBankNoName());
            row.createCell(5).setCellValue(form.getMemberAccountName());
            row.createCell(6).setCellValue(form.getMemberAccountNumber());
            row.createCell(7).setCellValue(form.getRegisterDt());
            row.createCell(8).setCellValue(form.getBillAt());
            row.createCell(9).setCellValue(form.getConfirmAt());
        }

        // 엑셀 파일을 ByteArrayOutputStream으로 쓰기
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=calculate_list.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

    @GetMapping("/mileage/refund")
    @ResponseMessage("환불 목록 조회 성공")
    @Operation(summary = "환불 목록", description = "환불 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "searchType", description = "조회 타입 A:마일리지환불, B:현금환불", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "searchKeyword", description = "검색어 ID / 닉네임 / 이름 / 신고내용", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색시작일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색종료일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "statusFilter", description = "전체 / 비활성화 / 활성화 필터", example = "",  schema = @Schema(type = "string"))
    })
    public AdminRefundListVo getRefundList(@ModelAttribute AdminMileageDto adminMileageDto) {
        return adminMileageService.getRefundList(adminMileageDto);
    }

    @GetMapping("/mileage/refund/excel")
    public ResponseEntity<byte[]> getRefundExcel(@ModelAttribute AdminMileageDto adminMileageDto) throws IOException {
        // 엑셀 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("환불내역");

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = {"국가 타입", "환불 금액", "환불요청일", "환불일","환불자 ID", "환불자 닉네임" , "환불자 이름", "환불사유"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }
        // 데이터 추가
        AdminRefundListVo list = adminMileageService.getRefundList(adminMileageDto);
        List<AdminRefundVo> formList =  list.getList();

        int rowNum = 1;
        for (AdminRefundVo form : formList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(form.getMileageUnitName());
            row.createCell(1).setCellValue(form.getMileageAmount());
            row.createCell(2).setCellValue(form.getRegisterDt());
            row.createCell(3).setCellValue(form.getMileageRefundConfirmDt());
            row.createCell(4).setCellValue(form.getMemberEmail());
            row.createCell(5).setCellValue(form.getMemberNickName());
            row.createCell(6).setCellValue(form.getMemberName());
            row.createCell(7).setCellValue(form.getMileageRefundCodeNoName());
        }

        // 엑셀 파일을 ByteArrayOutputStream으로 쓰기
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=refund_list.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

    @GetMapping("/mileage/deposit")
    @ResponseMessage("의뢰인 마일리지 환불 목록 조회 성공")
    @Operation(summary = "의뢰인 마일리지 환불 목록", description = "의뢰인 마일리지 환불 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "searchKeyword", description = "검색어 ID / 닉네임 / 이름 / 신고내용", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색시작일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색종료일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "statusFilter", description = "전체 / 비활성화 / 활성화 필터", example = "",  schema = @Schema(type = "string"))
    })
    public AdminDepositListVo getDepositList(@ModelAttribute AdminMileageDto adminMileageDto) {
        return adminMileageService.getDepositList(adminMileageDto);
    }

    @GetMapping("/mileage/deposit/excel")
    public ResponseEntity<byte[]> getDepositExcel(@ModelAttribute AdminMileageDto adminMileageDto) throws IOException {
        // 엑셀 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("의뢰인 마일리지 환불내역");

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = {"국가 타입", "입금 마일리지", "요청서 명", "입금한 의뢰인 ID","입금한 의뢰인 닉네임", "입금한 의뢰인 이름" , "입금일자"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }
        // 데이터 추가
        AdminDepositListVo list = adminMileageService.getDepositList(adminMileageDto);
        List<AdminDepositVo> formList =  list.getList();

        int rowNum = 1;
        for (AdminDepositVo form : formList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(form.getMileageUnitName());
            row.createCell(1).setCellValue(form.getMileageAmount());
            row.createCell(2).setCellValue(form.getRequestFormSj());
            row.createCell(3).setCellValue(form.getMemberEmail());
            row.createCell(4).setCellValue(form.getMemberNickName());
            row.createCell(5).setCellValue(form.getMemberName());
            row.createCell(6).setCellValue(form.getRegisterDt());
        }

        // 엑셀 파일을 ByteArrayOutputStream으로 쓰기
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=deposit_list.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

    @PutMapping("/mileage/refund-approval/{mileageNoArr}")
    @ResponseMessage("환불 승인 성공")
    @Operation(summary = "환불 승인", description = "환불을 승인한다.")
    @Parameter(name = "mileageNoArr", description = "환불 NO", example = "", schema = @Schema(type = "String"))
    public int putApprovalRefund(@PathVariable("mileageNoArr") String mileageNoArr) {
        return adminMileageService.putApprovalRefund(mileageNoArr);
    }

    @PutMapping("/mileage/calculate/{calculateNo}/{stat}")
    @ResponseMessage("정산 완료/취소 성공")
    @Operation(summary = "정산 완료/취소", description = "정산을 완료/취소한다.")
    @Parameter(name = "calculateNo", description = "정산 NO", example = "", schema = @Schema(type = "String"))
    public int putCalculateConfirm(@PathVariable("calculateNo") Integer calculateNo, @PathVariable("stat") String stat) {
        return adminMileageService.putCalculateConfirm(calculateNo, stat);
    }

    @PutMapping("/mileage/bill/{calculateNo}")
    @ResponseMessage("계산서발행 완료 성공")
    @Operation(summary = "계산서발행 완료", description = "계산서발행 여부를 완료한다.")
    @Parameter(name = "calculateNo", description = "정산 NO", example = "", schema = @Schema(type = "String"))
    public int putCalculateBillConfirm(@PathVariable("calculateNo") Integer calculateNo) {
        return adminMileageService.putCalculateBillConfirm(calculateNo);
    }

    @GetMapping("/mileage/calculate-group")
    @ResponseMessage("정산 그룹 목록 조회 성공")
    @Operation(summary = "정산 그룹 목록", description = "정산 그룹 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "searchKeyword", description = "검색어 ID / 닉네임 / 이름 / 신고내용", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색시작일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색종료일자", example = "",  schema = @Schema(type = "string"))
    })
    public AdminCalculateGroupListVo getCalculateGroupList(@ModelAttribute AdminMileageDto adminMileageDto) {
        return adminMileageService.getCalculateGroupList(adminMileageDto);
    }

    @PutMapping("/mileage/calculate-group/{calculateGroupNo}")
    @ResponseMessage("그룹 정산 완료 성공")
    @Operation(summary = "정산 완료", description = "정산을 완료한다.")
    @Parameter(name = "calculateNo", description = "정산 NO", example = "", schema = @Schema(type = "String"))
    public int putCalculateGroupConfirm(@PathVariable("calculateGroupNo") Integer calculateGroupNo) {
        return adminMileageService.putCalculateGroupConfirm(calculateGroupNo);
    }
}
