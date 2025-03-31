package com.dentner.admin.api.code;

import com.dentner.admin.version.V1ApiVersion;
import com.dentner.core.cmmn.dto.CodeAddDto;
import com.dentner.core.cmmn.dto.CodeDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.CodeListVo;
import com.dentner.core.cmmn.vo.CodeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "코드 api", description = "코드 API")
public class AdminCodeController implements V1ApiVersion {

	@Resource(name= "adminCodeService")
    AdminCodeService adminCodeService;
    @GetMapping({"/code", "/code/{parentNo}"})
    @ResponseMessage("코드 목록 조회 성공")
    @Operation(summary = "코드 목록", description = "코드 목록을 조회한다.")
    @Parameter(name = "parentNo", description = "상위 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public CodeListVo getCodeList(@PathVariable(required = false) Integer parentNo, @ModelAttribute CodeDto codeDto) {
        return adminCodeService.getCodeList(parentNo, codeDto);
    }

    @GetMapping("/code/detail/{codeNo}")
    @ResponseMessage("코드 상세 조회 성공")
    @Operation(summary = "코드 상세", description = "코드 상세를 조회한다.")
    @Parameter(name = "codeNo", description = "코드 NO", example = "" , schema = @Schema(type = "integer", format = "int32"))
    public CodeVo getCodeDetail(@PathVariable("codeNo") int codeNo) {
        return adminCodeService.getCodeDetail(codeNo);
    }

    @PostMapping("/code")
    @ResponseMessage("코드 등록 성공")
    @Operation(summary = "코드 등록", description = "코드를 추가한다.")
    @Parameters({
            @Parameter(name = "codeName", description = "코드명", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "codeTp", description = "코드 구분", example = "" ,  schema = @Schema(type = "string"))
    })
    public CodeAddDto postCode(@ModelAttribute CodeAddDto codeAddDto){
        return adminCodeService.postCode(codeAddDto);
    }

    @PutMapping("/code/{codeNo}")
    @ResponseMessage("코드 수정 성공")
    @Operation(summary = "코드 수정", description = "코드를 수정한다.")
    @Parameters({
            @Parameter(name = "codeNo", description = "코드 NO", example = "" , schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "codeName", description = "코드명", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "codeTp", description = "코드 구분", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "detailList", description = "중분류 데이터 list", example = "" ,  schema = @Schema(type = "string"))
    })
    public CodeAddDto putCode(@RequestBody CodeAddDto codeAddDto,
                                    @PathVariable("codeNo") int codeNo){
        return adminCodeService.putCode(codeAddDto, codeNo);
    }

    @DeleteMapping("/code/{codeNo}")
    @ResponseMessage("코드 삭제 성공")
    @Operation(summary = "코드 삭제", description = "코드를 삭제한다.")
    @Parameter(name = "codeNo", description = "코드 NO", example = "" , schema = @Schema(type = "integer", format = "int32"))
    public int deleteCode(@PathVariable("codeNo") int codeNo) {
        return adminCodeService.deleteCode(codeNo);
    }

}
