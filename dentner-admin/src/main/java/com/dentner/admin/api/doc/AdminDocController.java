package com.dentner.admin.api.doc;


import com.dentner.admin.version.V1ApiVersion;
import com.dentner.core.cmmn.dto.AdminDocDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.AdminDocListVo;
import com.dentner.core.cmmn.vo.AdminDocVo;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Tag(name = "관리자 의뢰서 관리", description = "관리자 의뢰서 API")
public class AdminDocController implements V1ApiVersion {

	@Resource(name= "adminDocService")
    AdminDocService adminDocService;

    @GetMapping("/doc/{requestSe}")
    @ResponseMessage("의뢰서 목록 조회 성공")
    @Operation(summary = "의뢰서 목록", description = "의뢰서 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "searchKeyword", description = "검색어 ID / 닉네임 / 이름 / 신고내용", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "searchType", description = "검색 타입(A:공개, B:지정)", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색시작일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색종료일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "statusFilter", description = "전체 / 비활성화 / 활성화 필터", example = "",  schema = @Schema(type = "string"))
    })
    public AdminDocListVo getDocList(@PathVariable("requestSe") String requestSe, @ModelAttribute AdminDocDto adminDocDto) {
        return adminDocService.getDocList(requestSe, adminDocDto);
    }

    @GetMapping("/doc/detail/{requestDocGroupNo}")
    @ResponseMessage("의뢰서 상세 조회 성공")
    @Operation(summary = "의뢰서 상세", description = "의뢰서 상세를 조회한다.")
    @Parameter(name = "requestDocGroupNo", description = "의뢰서 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public AdminDocVo getDocDetail(@PathVariable("requestDocGroupNo") int requestDocGroupNo) {
        return adminDocService.getDocDetail(requestDocGroupNo);
    }

    @GetMapping("/doc/{requestSe}/download/excel")
    @ResponseMessage("의뢰서 엑셀 다운로드 성공")
    @Operation(summary = "의뢰서 엑셀 다운로드", description = "의뢰서 엑셀을 다운로드한다.")
    @Parameter(name = "requestSe", description = "의뢰서 구분", example = "",  schema = @Schema(type = "string"))
    public ResponseEntity<byte[]> downloadExcel(@PathVariable("requestSe") String requestSe, @ModelAttribute AdminDocDto adminDocDto) throws IOException {
        // 엑셀 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("거래내역");

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = {"요청 타입", "요청서 명", "의뢰번호", "보철종류","보철종류별 갯수", "금액" , "(작성자) 의뢰인 닉네임", "(작성자) 의뢰인 ID", "치자이너 닉네임", "치자이너 ID", "작성일"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // 데이터 추가
        AdminDocListVo list = adminDocService.getDocList(requestSe, adminDocDto);
        List<AdminDocVo> formList =  list.getList();


        int rowNum = 1;
        for (AdminDocVo form : formList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("B".equals(form.getRequestFormSe()) ? "지정요청" : "공개요청");
            row.createCell(1).setCellValue(form.getRequestFormSj());
            row.createCell(2).setCellValue(form.getRequestNumber());

            AtomicInteger count = new AtomicInteger();
            ArrayList<String> requestList = new ArrayList<>();
            form.getProstheticsList().forEach((e) -> {
                String requestTypeName = e.get("requestTypeName").toString();
                String requestCount = e.get("count").toString();

                requestList.add(requestTypeName);
                count.addAndGet(Integer.parseInt(requestCount));
            });

            String result = String.join(",", requestList);
            row.createCell(3).setCellValue(result);

            row.createCell(4).setCellValue(count.get()+"");
            row.createCell(5).setCellValue(count.get() * 5000 + "");
            row.createCell(6).setCellValue(form.getMemberNickName());
            row.createCell(7).setCellValue(form.getMemberEmail());
            row.createCell(8).setCellValue(form.getDesignerNickName());
            row.createCell(9).setCellValue(form.getDesignerEmail());

            row.createCell(9).setCellValue(form.getRegisterDt());

        }

        // 엑셀 파일을 ByteArrayOutputStream으로 쓰기
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=docrequest.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }
}
