package com.dentner.front.api.join;


import com.dentner.core.cmmn.dto.MemberAddDto;
import com.dentner.core.cmmn.dto.PasswordDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.FrontTermsVo;
import com.dentner.front.version.V1ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@RestController
@Tag(name = "회원가입 api", description = "회원가입 API")
public class JoinController implements V1ApiVersion {

	@Resource(name= "joinService")
    JoinService joinService;

    @GetMapping("/join/terms")
    @ResponseMessage("약관 조회 성공")
    @Operation(summary = "약관 조회", description = "약관을 조회한다.")
    public FrontTermsVo getTerms() {
        return joinService.getTerms();
    }

    @PostMapping("/join/local/member")
    @ResponseMessage("내국인 회원가입 성공")
    @Operation(summary = "내국인 회원가입", description = "내국인 회원가입을 한다.")
    @Parameters({
            @Parameter(name = "memberEmail", description = "E-mail", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberName", description = "Name", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberSe", description = "회원 구분", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberPassword", description = "Password", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberHpNation", description = "핸드폰 국가", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "memberDentistryName", description = "대표자명", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberHp", description = "핸드폰 번호", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberNickName", description = "닉네임", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberLicenseNumber", description = "면허 번호", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberDentistryName", description = "치과명", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberBusinessNumber", description = "사업자 등록 번호", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberAddress", description = "사업장 주소", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberDetailAddress", description = "사업장 상세 주소", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "socialSe", description = "소셜 type", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "socialUniqueKey", description = "소셜 유니크 키", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "memberBankNo", description = "은행 코드", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "memberAccountName", description = "예금주", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "memberAccountNumber", description = "계좌번호", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "memberMarketingAt", description = "마케팅수신여부", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "licenseFile", description = "치과의사 면허증", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "businessFile", description = "사업자 등록증", example = "" ,  schema = @Schema(type = "string"))
    })
    public MemberAddDto postMember(@RequestParam(required = false) MultipartFile licenseFile,
                                   @RequestParam(required = false) MultipartFile businessFile,
                                   @ModelAttribute MemberAddDto memberAddDto) throws Exception{
        return joinService.postMember(licenseFile, businessFile, memberAddDto);
    }

    @PostMapping("/join/country/member")
    @ResponseMessage("외국인 회원가입 성공")
    @Operation(summary = "외국인 회원가입", description = "외국인 회원가입을 한다.")
    @Parameters({
            @Parameter(name = "memberEmail", description = "E-mail", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberFirstName", description = "영문 이름", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberLastName", description = "영문 성", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberPassword", description = "Password", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberHpNation", description = "핸드폰 국가", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "memberHp", description = "핸드폰 번호", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberNickName", description = "닉네임", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberAlarmAt", description = "알림톡 사용 여부", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberAlarmSe", description = "알림톡 구분", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberJobSe", description = "직업 구분", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberBusinessName", description = "사업자 명", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberBusinessNumber", description = "사업자 등록 번호", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberAddress", description = "사업장 주소", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberDetailAddress", description = "사업장 상세 주소", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "socialSe", description = "소셜 type", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "socialUniqueKey", description = "소셜 유니크 키", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "memberMarketingAt", description = "마케팅수신여부", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "memberTimezoneNo", description = "타임존 코드", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "businessFile", description = "사업자 등록증", example = "" ,  schema = @Schema(type = "string"))
    })
    public MemberAddDto postCountryMember(@RequestParam(required = false) MultipartFile businessFile,
                                   @ModelAttribute MemberAddDto memberAddDto) throws Exception{
        return joinService.postCountryMember(businessFile, memberAddDto);
    }

    @GetMapping("/join/dup/nickname")
    @ResponseMessage("닉네임 중복 조회 성공")
    @Operation(summary = "닉네임 중복 조회 조회", description = "닉네임 중복을 조회한다.")
    @Parameter(name = "memberNickName", description = "닉네임", example = "",  schema = @Schema(type = "string"))
    public int getDubNickname(@ModelAttribute MemberAddDto memberAddDto) {
        return joinService.getDupNickName(memberAddDto);
    }

    @GetMapping("/join/dup/email")
    @ResponseMessage("이메일 중복 조회 성공")
    @Operation(summary = "이메일 중복 조회 조회", description = "이메일 중복을 조회한다.")
    @Parameter(name = "memberEmail", description = "E-mail", example = "",  schema = @Schema(type = "string"))
    public int getDubEmail(@ModelAttribute MemberAddDto memberAddDto) {
        return joinService.getDupEmail(memberAddDto);
    }

    @GetMapping("/join/email")
    @ResponseMessage("이메일 정보 조회")
    @Operation(summary = "이메일 정보 조회", description = "이메일 정보를 조회한다.")
    @Parameters({
            @Parameter(name = "t", description = "토큰", example = "",  schema = @Schema(type = "string"))
    })
    public HashMap<String, Object> getId(@RequestParam String t) throws Exception{
        HashMap<String, Object> result = new HashMap<String, Object>();
        String email = joinService.getId(t);
        result.put("email", email);

        return result;
    }

    @PutMapping("/join/password")
    @ResponseMessage("비밀번호 변경 성공")
    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경 한다.")
    @Parameters({
            @Parameter(name = "token", description = "토큰", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "password", description = "비밀번호", example = "",  schema = @Schema(type = "string"))
    })
    public int putPassword(@RequestBody PasswordDto passwordDto) throws Exception{
        return joinService.putPassword(passwordDto);
    }

}
