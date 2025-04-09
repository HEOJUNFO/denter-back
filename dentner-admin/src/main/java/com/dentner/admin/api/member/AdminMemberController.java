package com.dentner.admin.api.member;


import com.dentner.admin.version.V1ApiVersion;
import com.dentner.core.cmmn.dto.MemberAddDto;
import com.dentner.core.cmmn.dto.MemberDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.MemberListVo;
import com.dentner.core.cmmn.vo.MemberVo;
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
@Tag(name = "관리자 회원 관리", description = "관리자 회원 API")
public class AdminMemberController implements V1ApiVersion {

	@Resource(name= "adminMemberService")
    AdminMemberService adminMemberService;

    @GetMapping("/member")
    @ResponseMessage("회원 목록 조회 성공")
    @Operation(summary = "회원 목록", description = "회원 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "searchKeyword", description = "검색어", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색시작일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색종료일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "registerSe", description = "가입유형구분(A:이메일가입, B:소셜가입)", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberSe", description = "회원 구분 (A:의뢰인, B:치과기공소, C:치자이너)", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberTp", description = "회원 유형 (A:한국인, B:외국인)", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "searchSe", description = "목록 구분(A:가입승인 대기, B:이용회원, C:탈퇴승인 대기, D:거래이력 있는 탈퇴회원, E:일반탈퇴회원, F:블랙리스트 회원)", example = "",  schema = @Schema(type = "string")),
    })
    public MemberListVo getMemberList(@ModelAttribute MemberDto memberDto) {
        return adminMemberService.getMemberList(memberDto);
    }

    @GetMapping("/member/detail/{memberNo}")
    @ResponseMessage("회원 상세 조회 성공")
    @Operation(summary = "회원 상세", description = "회원 상세를 조회한다.")
    @Parameter(name = "memberNo", description = "회원 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public MemberVo getMemberDetail(@PathVariable("memberNo") int memberNo) {
        return adminMemberService.getMemberDetail(memberNo);
    }

    @PutMapping("/member/{memberNo}")
    @ResponseMessage("회원 수정 성공")
    @Operation(summary = "회원 수정", description = "회원을 수정한다.")
    @Parameters({
            @Parameter(name = "memberNo", description = "회원 NO", example = "", schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "memberEmail", description = "E-mail", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberName", description = "Name", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberPassword", description = "Password", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberContact1Nation", description = "Contact number Nation", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "memberContact1", description = "Contact number", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberNationality", description = "Nationality", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "memberLanguage", description = "Fluent Languages", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberBirthday", description = "Date of birth", example = "",  schema = @Schema(type = "string"))
    })
    public MemberAddDto putMember(@RequestBody MemberAddDto memberAddDto,
                           @PathVariable("memberNo") int memberNo) {
        return adminMemberService.putMember(memberNo, memberAddDto);
    }

    @DeleteMapping("/member/{memberNo}")
    @ResponseMessage("회원 삭제 성공")
    @Operation(summary = "회원 삭제", description = "회원을 삭제한다.")
    @Parameter(name = "memberNo", description = "회원 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public int deleteMember(@PathVariable("memberNo") int memberNo) {
        return adminMemberService.deleteMember(memberNo);
    }

    @PutMapping("/member/approval/{memberNoArr}")
    @ResponseMessage("회원 등록 승인 성공")
    @Operation(summary = "회원 등록 승인", description = "회원 등록을 승인한다.")
    @Parameter(name = "memberNoArr", description = "회원 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public int putApprovalMember(@PathVariable("memberNoArr") String memberNoArr) throws Exception{
        return adminMemberService.putApprovalMember(memberNoArr);
    }

    @PutMapping("/member/reject/{memberNoArr}")
    @ResponseMessage("회원 등록 거절 성공")
    @Operation(summary = "회원 등록 거절", description = "회원 등록을 거절한다.")
    @Parameter(name = "memberNoArr", description = "회원 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public int putApprovalRejectMember(@PathVariable("memberNoArr") String memberNoArr) throws Exception{
        return adminMemberService.putApprovalRejectMember(memberNoArr);
    }

    @PutMapping("/member/out/approval/{memberNo}")
    @ResponseMessage("탈퇴회원 승인 성공")
    @Operation(summary = "탈퇴 회원 승인", description = "탈퇴회원을 승인한다.")
    @Parameter(name = "memberNo", description = "회원 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public int putOutApprovalMember(@PathVariable("memberNo") int memberNo) {
        return adminMemberService.putOutApprovalMember(memberNo);
    }

    @GetMapping("/member/excel")
    @ResponseMessage("회원 목록 조회 성공")
    @Operation(summary = "회원 목록", description = "회원 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "searchKeyword", description = "검색어", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "fromDateFilter", description = "검색시작일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "toDateFilter", description = "검색종료일자", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "registerSe", description = "가입유형구분(A:이메일가입, B:소셜가입)", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberSe", description = "회원 구분 (A:의뢰인, B:치과기공소, C:치자이너)", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberTp", description = "회원 유형 (A:한국인, B:외국인)", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "searchSe", description = "목록 구분(A:가입승인 대기, B:이용회원, C:탈퇴승인 대기, D:거래이력 있는 탈퇴회원, E:일반탈퇴회원, F:블랙리스트 회원)", example = "",  schema = @Schema(type = "string")),
    })
    public ResponseEntity<byte[]> getMemberExcelList(@ModelAttribute MemberDto memberDto) throws IOException {
        // 엑셀 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("회원 이용내역");

        String memberSe = memberDto.getMemberSe();
        String memberTp = memberDto.getMemberTp();
        List<String> headerList = new ArrayList<String>();

        if("A".equals(memberSe) && "A".equals(memberTp)){ // 국내 의뢰인
            headerList.add("가입타입");
            headerList.add("닉네임");
            headerList.add("이름");
            headerList.add("휴대폰 번호");
            headerList.add("치과명");
            headerList.add("면허번호");
            headerList.add("사업자 등록번호");
            headerList.add("가입일");
        }else if("A".equals(memberSe) && "B".equals(memberTp)){
            headerList.add("가입타입");
            headerList.add("닉네임");
            headerList.add("성");
            headerList.add("이름");
            headerList.add("휴대폰 번호");
            headerList.add("직업");
            headerList.add("사업자명");
            headerList.add("사업자 등록번호");
            headerList.add("가입일");
        }
        if("B".equals(memberSe)){
            headerList.add("가입타입");
            headerList.add("닉네임");
            headerList.add("휴대폰 번호");
            headerList.add("상호명");
            headerList.add("대표자명");
            headerList.add("면허번호");
            headerList.add("사업자 등록번호");
            headerList.add("가입일");
        }else if("C".equals(memberSe)){
            headerList.add("가입타입");
            headerList.add("닉네임");
            headerList.add("이름");
            headerList.add("휴대폰 번호");
            headerList.add("상호명");
            headerList.add("대표자명");
            headerList.add("사업자명");
            headerList.add("면허번호");
            headerList.add("사업자 등록번호");
            headerList.add("가입일");
        }
        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = headerList.toArray(new String[0]);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // 데이터 추가
        List<MemberVo> formList = adminMemberService.getMemberExcelList(memberDto);

        int rowNum = 1;
        for (MemberVo form : formList) {
            Row row = sheet.createRow(rowNum++);
            if("A".equals(memberSe) && "A".equals(memberTp)){ // 국내 의뢰인
                row.createCell(0).setCellValue(form.getSocialSeName());
                row.createCell(1).setCellValue(form.getMemberNickName());
                row.createCell(2).setCellValue(form.getMemberName());
                row.createCell(3).setCellValue(form.getMemberHp());
                row.createCell(4).setCellValue(form.getMemberDentistryName());
                row.createCell(5).setCellValue(form.getMemberLicenseNumber());
                row.createCell(6).setCellValue(form.getMemberBusinessNumber());
                row.createCell(7).setCellValue(form.getRegisterDt());
            }else if("A".equals(memberSe) && "B".equals(memberTp)){
                row.createCell(0).setCellValue(form.getSocialSeName());
                row.createCell(1).setCellValue(form.getMemberNickName());
                row.createCell(2).setCellValue(form.getMemberFirstName());
                row.createCell(3).setCellValue(form.getMemberLastName());
                row.createCell(4).setCellValue(form.getMemberHp());
                row.createCell(5).setCellValue(form.getMemberJobSeName());
                row.createCell(6).setCellValue(form.getMemberBusinessName());
                row.createCell(7).setCellValue(form.getMemberBusinessNumber());
                row.createCell(8).setCellValue(form.getRegisterDt());
            }
            if("B".equals(memberSe)){
                row.createCell(0).setCellValue(form.getSocialSeName());
                row.createCell(1).setCellValue(form.getMemberNickName());
                row.createCell(2).setCellValue(form.getMemberHp());
                row.createCell(3).setCellValue(form.getMemberDentistryName());
                row.createCell(4).setCellValue(form.getMemberRepresentativeName());
                row.createCell(5).setCellValue(form.getMemberLicenseNumber());
                row.createCell(6).setCellValue(form.getMemberBusinessNumber());
                row.createCell(7).setCellValue(form.getRegisterDt());
            }else if("C".equals(memberSe)){
                row.createCell(0).setCellValue(form.getSocialSeName());
                row.createCell(1).setCellValue(form.getMemberNickName());
                row.createCell(2).setCellValue(form.getMemberName());
                row.createCell(3).setCellValue(form.getMemberHp());
                row.createCell(4).setCellValue(form.getMemberDentistryName());
                row.createCell(5).setCellValue(form.getMemberRepresentativeName());
                row.createCell(6).setCellValue(form.getMemberLicenseNumber());
                row.createCell(7).setCellValue(form.getMemberBusinessNumber());
                row.createCell(8).setCellValue(form.getRegisterDt());
            }
        }

        // 엑셀 파일을 ByteArrayOutputStream으로 쓰기
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=member.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

}
