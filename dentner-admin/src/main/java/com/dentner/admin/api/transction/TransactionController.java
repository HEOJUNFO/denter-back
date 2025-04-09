package com.dentner.admin.api.transction;


import com.dentner.admin.version.V1ApiVersion;
import com.dentner.core.cmmn.dto.AdminRequestFormDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.AdminRequestFormListVo;
import com.dentner.core.cmmn.vo.AdminRequestFormVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin 거래관리 api", description = "Admin 거래관리 API")
public class TransactionController implements V1ApiVersion {
	@Resource(name= "transactionService")
    TransactionService transactionService;

    @GetMapping("/transaction/{requestFormSe}")
    @ResponseMessage("거래내역 목록 성공")
    @Operation(summary = "거래내역 목록 조회", description = "거래내역 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "statusFilter", description = "상태 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색 시작 일자 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색 종료 일자 필터", example = "" ,  schema = @Schema(type = "string"))
    })
    public AdminRequestFormListVo getTransactionFormList(@PathVariable(required = false) String requestFormSe, @ModelAttribute AdminRequestFormDto adminRequestFormDto) {
        return transactionService.getTransactionFormList(requestFormSe, adminRequestFormDto);
    }

    @GetMapping("/transaction/detail/{type}/{requestFormNo}")
    @ResponseMessage("거래내역 상세 조회 성공")
    @Operation(summary = "거래내역 상세", description = "거래내역 상세를 조회한다.")
    @Parameters({
            @Parameter(name = "type", description = "타입 A:공개, B:지정", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "requestFormNo", description = "거래내역 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    })
    public AdminRequestFormVo getTransactionDetail(@PathVariable("type") String type, @PathVariable("requestFormNo") int requestFormNo) {
        return transactionService.getTransactionDetail(type, requestFormNo);
    }

    @GetMapping("/transaction/{requestFormSe}/download/excel")
    @Parameters({
            @Parameter(name = "statusFilter", description = "상태 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색 시작 일자 필터", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색 종료 일자 필터", example = "" ,  schema = @Schema(type = "string"))
    })
    public ResponseEntity<byte[]> downloadExcel(@PathVariable("requestFormSe") String requestFormSe, @ModelAttribute AdminRequestFormDto adminRequestFormDto) throws IOException {
        // 엑셀 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("거래내역");

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = {"요청서 명", "의뢰인 닉네임", "의뢰인 이메일", "치자이너 닉네임","치자이너 이메일", "거래 진행상태", "보철종류", "갯수", "금액", "요청서 작성일"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // 데이터 추가
        List<AdminRequestFormVo> list = transactionService.getTransactionExcelList(requestFormSe, adminRequestFormDto);


        int rowNum = 1;
        for (AdminRequestFormVo form : list) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(form.getRequestFormSj());
            row.createCell(1).setCellValue(form.getMemberEmail());
            row.createCell(2).setCellValue(form.getDesignerEmail());

            row.createCell(3).setCellValue(form.getRequestNickName());

            row.createCell(4).setCellValue(form.getDesignerNickName());
            row.createCell(5).setCellValue(form.getMileageStatus());

            row.createCell(6).setCellValue(form.getRequestTypeName());
            row.createCell(7).setCellValue(form.getCount());
            row.createCell(8).setCellValue(form.getMileageAmount());

            row.createCell(9).setCellValue(form.getRegisterDt());

        }

        // 엑셀 파일을 ByteArrayOutputStream으로 쓰기
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transaction.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

}
