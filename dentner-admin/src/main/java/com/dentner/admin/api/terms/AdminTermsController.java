package com.dentner.admin.api.terms;


import com.dentner.admin.version.V1ApiVersion;
import com.dentner.core.cmmn.dto.TermsAddDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.TermsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Admin 회원가입", description = "Admin 회원가입 API")
public class AdminTermsController implements V1ApiVersion {

    @Resource(name="adminTermsService")
    AdminTermsService adminTermsService;

    @GetMapping("/terms/{termsSe}")
    @ResponseMessage("약관 정보 조회 성공")
    @Operation(summary = "약관 정보", description = "약관 정보를 조회한다.")
    @Parameter(name = "termsSe", description = "약관 구분 " +
            "A :개인정보 수집 및 이용 동의 입니다.\n" +
            "B :서비스 이용약관 : \n" +
            "C :위치정보 서비스 이용약관\n" +
            "D :제3자 정보제공 \n" , example = "",  schema = @Schema(type = "string"))
    public TermsVo getTermsInfo(@PathVariable("termsSe") String termsSe) {
        return adminTermsService.getTermsInfo(termsSe);
    }

    @PutMapping("/terms/{termsSe}")
    @ResponseMessage("약관 수정 성공")
    @Operation(summary = "약관 수정", description = "약관을 수정한다.")
    @Parameters({
            @Parameter(name = "termsSe", description = "약관 구분", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "termsCn", description = "약관 내용", example = "",  schema = @Schema(type = "string"))
    })
    public int putTermsInfo(@PathVariable("termsSe") String termsSe, @RequestBody TermsAddDto termsAddDto){
        return adminTermsService.putTermsInfo(termsSe, termsAddDto);
    }
}
