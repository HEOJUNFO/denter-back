package com.dentner.admin.api.bbs;


import com.dentner.admin.version.V1ApiVersion;
import com.dentner.core.cmmn.dto.BbsAddDto;
import com.dentner.core.cmmn.dto.BbsDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.BbsListVo;
import com.dentner.core.cmmn.vo.BbsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "관리자 고객센터 관리", description = "관리자 고객센터 API")
public class AdminBbsController implements V1ApiVersion {

	@Resource(name= "adminBbsService")
    AdminBbsService adminBbsService;

    @GetMapping("/bbs")
    @ResponseMessage("게시글 목록 조회 성공")
    @Operation(summary = "게시글 목록", description = "게시글 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "bbsSe", description = "", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "searchKeyword", description = "", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "statusFilter", description = "", example = "",  schema = @Schema(type = "string"))
    })
    public BbsListVo getBbsList(@ModelAttribute BbsDto bbsDto) {
        return adminBbsService.getBbsList(bbsDto);
    }

    @GetMapping("/bbs/detail/{bbsNo}")
    @ResponseMessage("게시글 상세 조회 성공")
    @Operation(summary = "게시글 상세", description = "게시글 상세를 조회한다.")
    @Parameters({
            @Parameter(name = "bbsSe", description = "", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "bbsNo", description = "게시판 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    })

    public BbsVo getBbsDetail(@PathVariable("bbsNo") Integer bbsNo) {
        return adminBbsService.getBbsDetail(bbsNo);
    }

    @PostMapping("/bbs/{bbsSe}")
    @ResponseMessage("게시글 등록 성공")
    @Operation(summary = "게시글 등록", description = "게시글을 등록한다.")
    @Parameter(name = "bbsSe", description = "", example = "", schema = @Schema(type = "string"))
    public int postBbs(@PathVariable("bbsSe") String bbsSe, @RequestBody BbsAddDto bbsAddDto) {
        return adminBbsService.postBbs(bbsSe, bbsAddDto);
    }

    @PutMapping("/bbs/{bbsNo}")
    @ResponseMessage("게시글 수정 성공")
    @Operation(summary = "게시글 수정", description = "게시글을 수정한다.")
    @Parameter(name = "bbsNo", description = "게시판 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public int putBbs(@PathVariable("bbsNo") Integer bbsNo, @RequestBody BbsAddDto bbsAddDto) {
        return adminBbsService.putBbs(bbsNo, bbsAddDto);
    }

    @DeleteMapping("/bbs/{bbsNoArr}")
    @ResponseMessage("게시글 삭제 성공")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제한다.")
    @Parameter(name = "bbsNo", description = "게시판 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public int deleteBbs(@PathVariable("bbsNoArr") String bbsNoArr) {
        return adminBbsService.deleteBbs(bbsNoArr);
    }

    @PutMapping("/bbs/fix/{bbsNoArr}/{type}/{bbsTp}")
    @ResponseMessage("게시글 상단 고정 성공")
    @Operation(summary = "게시글 상단 고정", description = "게시글을 상단 고정한다.")
    @Parameter(name = "bbsNo", description = "게시판 NO", example = "", schema = @Schema(type = "integer", format = "int32"))
    public int putFixBbs(@PathVariable("bbsNoArr") String bbsNoArr, @PathVariable("type") String type, @PathVariable("bbsTp") String bbsTp) throws Exception{
        return adminBbsService.putFixBbs(bbsNoArr, type, bbsTp);
    }

}
