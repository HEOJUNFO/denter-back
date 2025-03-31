package com.dentner.admin.api.banner;

import com.dentner.admin.version.V1ApiVersion;
import com.dentner.core.cmmn.dto.BannerAddDto;
import com.dentner.core.cmmn.dto.BannerDto;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.BannerListVo;
import com.dentner.core.cmmn.vo.BannerVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Tag(name = "관리자 배너 관리", description = "관리자 배너 API")
public class AdminBannerController implements V1ApiVersion {

	@Resource(name= "adminBannerService")
    AdminBannerService adminBannerService;

    @GetMapping("/banner/{bannerSe}")
    @ResponseMessage("배너 목록 조회 성공")
    @Operation(summary = "배너 목록", description = "배너 목록을 조회한다.")
    @Parameter(name = "bannerSe", description = "배너 구분", example = "",  schema = @Schema(type = "string"))
    public BannerListVo getBoardList(@PathVariable("bannerSe") String bannerSe,@ModelAttribute BannerDto bannerDto) {
        return adminBannerService.getBannerList(bannerSe, bannerDto);
    }

    @GetMapping("/banner/detail/{bannerNo}")
    @ResponseMessage("배너 상세 조회 성공")
    @Operation(summary = "배너 상세", description = "배너 상세를 조회한다.")
    @Parameter(name = "bannerNo", description = "배너 NO", example = "" , schema = @Schema(type = "integer", format = "int32"))
    public BannerVo getBannerDetail(@PathVariable("bannerNo") int bannerNo) {
        return adminBannerService.getBannerDetail(bannerNo);
    }

    @PostMapping("/banner")
    @ResponseMessage("배너 등록 성공")
    @Operation(summary = "배너 등록", description = "배너를 등록한다.")
    @Parameters({
            @Parameter(name = "bannerTitle", description = "배너 제목", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "bannerStartDt", description = "배너 시작일시", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "bannerEndDt", description = "배너 종료일시", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "bannerSe", description = "배너 구분(A:메인,B:파트너사,C:하단)", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "bannerDesc", description = "배너 설명", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "bannerUrl", description = "배너 링크 URL", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "bannerOrdr", description = "정렬순서", example = "" , schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "files", description = "파일첨부", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "mobFiles", description = "파일첨부", example = "" ,  schema = @Schema(type = "string"))
    })
    public BannerAddDto postBanner(@RequestParam(required = false) List<MultipartFile> files,
                                   List<MultipartFile> mobFiles,
                                  @ModelAttribute BannerAddDto bannerAddDto) throws IOException {
        return adminBannerService.postBanner(files, mobFiles, bannerAddDto);
    }

    @PutMapping("/banner/{bannerNo}")
    @ResponseMessage("배너 수정 성공")
    @Operation(summary = "배너 수정", description = "배너를 수정한다.")
    @Parameters({
            @Parameter(name = "bannerTitle", description = "배너 제목", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "bannerStartDt", description = "배너 시작일시", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "bannerEndDt", description = "배너 종료일시", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "bannerSe", description = "배너 구분(A:메인,B:파트너사,C:하단)", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "bannerDesc", description = "배너 설명", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "bannerUrl", description = "배너 링크 URL", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "bannerOrdr", description = "정렬순서", example = "" , schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "files", description = "파일첨부", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "mobFiles", description = "파일첨부", example = "" ,  schema = @Schema(type = "string"))
    })
    public BannerAddDto putBanner(@RequestParam(required = false) List<MultipartFile> files,
                                  List<MultipartFile> mobFiles,
                            @ModelAttribute BannerAddDto bannerAddDto,
                           @PathVariable("bannerNo") int bannerNo) throws IOException {
        return adminBannerService.putBanner(bannerNo, files, mobFiles, bannerAddDto);
    }

    @DeleteMapping("/banner/{bannerNoArr}")
    @ResponseMessage("배너 삭제 성공")
    @Operation(summary = "배너 삭제", description = "배너를 삭제한다.")
    @Parameter(name = "bannerNoArr", description = "배너 NO", example = "" , schema = @Schema(type = "integer", format = "int32"))
    public int deleteBanner(@PathVariable("bannerNoArr") String bannerNoArr) {
        return adminBannerService.deleteBanner(bannerNoArr);
    }

    @PutMapping("/banner/ordr/{bannerNo}")
    @ResponseMessage("배너 정렬 수정 성공")
    @Operation(summary = "배너 정렬 수정", description = "배너 정렬 수정한다.")
    @Parameter(name = "sort", description = "정렬순서", example = "" , schema = @Schema(type = "integer", format = "int32"))
    public int putOrdrBanner(@ModelAttribute BannerAddDto bannerAddDto,
                                  @PathVariable("bannerNo") int bannerNo) throws IOException {
        return adminBannerService.putBannerOrdr(bannerNo, bannerAddDto);
    }

}
