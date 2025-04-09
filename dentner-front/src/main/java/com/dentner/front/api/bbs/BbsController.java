package com.dentner.front.api.bbs;


import com.dentner.core.cmmn.dto.BbsDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.BbsListVo;
import com.dentner.front.version.V1ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "고객센터 api", description = "고객센터 API")
public class BbsController implements V1ApiVersion {
	@Resource(name= "bbsService")
    BbsService bbsService;

    @GetMapping({"/bbs/{bbsSe}", "/bbs/{bbsSe}/{bbsTp}"})
    @ResponseMessage("고객센터 조회 성공")
    @Operation(summary = "고객센터 조회", description = "고객센터 정보를 조회한다.")
    @Parameters({
        @Parameter(name = "bbsSe", description = "게시글 구분", example = "",  schema = @Schema(type = "string")),
        @Parameter(name = "startRow", description = "페이징 시작번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "pageCnt", description = "페이징 카운트", example = "",  schema = @Schema(type = "integer", format = "int32")),
        @Parameter(name = "bbsTp", description = "FAQ 타입(A:공통,B:의뢰인,C:치과기공소,D:치자이너)", example = "",  schema = @Schema(type = "string"))
    })
    public BbsListVo getBbs(@PathVariable(required = false) String bbsSe,
                            @PathVariable(required = false) String bbsTp, @ModelAttribute BbsDto bbsDto) {
        return bbsService.getBbs(bbsSe, bbsTp, bbsDto);
    }

}
