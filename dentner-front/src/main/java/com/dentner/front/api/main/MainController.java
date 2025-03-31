package com.dentner.front.api.main;


import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.BannerVo;
import com.dentner.core.cmmn.vo.BbsVo;
import com.dentner.core.cmmn.vo.StatVo;
import com.dentner.front.version.V1ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "메인 api", description = "메인 API")
public class MainController implements V1ApiVersion {
	@Resource(name= "mainService")
    com.dentner.front.api.main.MainService mainService;

    @GetMapping("/main/banner")
    @ResponseMessage("배너 조회 성공")
    @Operation(summary = "배너 조회", description = "배너를 조회한다.")
    public List<BannerVo> getBanner() {
        return mainService.getBanner();
    }

    @GetMapping("/main/stat")
    @ResponseMessage("통계 조회 성공")
    @Operation(summary = "통계 조회", description = "통계정보를 조회한다.")
    public StatVo getStat() {
        return mainService.getStat();
    }

    @GetMapping({"/main/bbs/{bbsTp}", "/main/bbs"})
    @ResponseMessage("FAQ 조회 성공")
    @Operation(summary = "FAQ 조회", description = "FAQ 정보를 조회한다.")
    @Parameter(name = "bbsTp", description = "FAQ 타입", example = "",  schema = @Schema(type = "string"))
    public List<BbsVo> getBbs(@PathVariable(required = false) String bbsTp) {
        return mainService.getBbs(bbsTp);
    }

}
