package com.dentner.admin.api.common;

import com.dentner.admin.api.file.S3DownloadService;
import com.dentner.admin.version.V1ApiVersion;
import com.dentner.core.cmmn.dto.AlarmTalkDto;
import com.dentner.core.cmmn.dto.FileDto;
import com.dentner.core.cmmn.service.FileService;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.CodeVo;
import com.dentner.core.cmmn.vo.FileVO;
import com.dentner.core.cmmn.vo.S3FileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "공통 api", description = "공통 API")
public class CommonController implements V1ApiVersion {

	@Resource(name= "commonService")
    CommonService commonService;

    @Resource(name= "fileService")
    FileService fileService;

    private final S3DownloadService s3DownloadService;

    @GetMapping({"/common/code", "/common/code/{parentNo}"})
    @ResponseMessage("코드 목록 조회 성공")
    @Operation(summary = "코드 목록", description = "코드 목록을 조회한다.")
    @Parameter(name = "parentNo", description = "상위 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public List<CodeVo> getCodeList(@PathVariable(required = false) Integer parentNo, @RequestParam String type) {
        return commonService.getCodeList(parentNo, type);
    }

    @PostMapping("/common/edit/file")
    @ResponseMessage("에디트 이미지 등록 성공")
    @Operation(summary = "에디트 이미지 등록", description = "에디트 이미지를 등록한다.")
    public FileVO postEditFile(@RequestBody FileDto fileDto) throws IOException {
        return commonService.postEditFile(fileDto);
    }

    @GetMapping("/common/download/{fileNo}")
    @Operation(summary = "파일다운로드", description = "파일을 다운로드 한다.")
    @Parameter(name = "fileNo", description = "파일 NO", example = "" , schema = @Schema(type = "integer", format = "int32"))
    public ResponseEntity<ByteArrayResource> getFileDownload(@PathVariable("fileNo") Integer fileNo, HttpServletRequest req) throws Exception {
        S3FileVO fileVO = fileService.selectFile(fileNo);
        String fileKey = fileVO.getFileName();
        String downloadFileName = fileVO.getFileRealName();

        return s3DownloadService.getObject(fileKey, downloadFileName, req);
    }

    @PostMapping( "/common/kakao/send")
    @ResponseMessage("카카오 알림톡 전송")
    @Operation(summary = "카카오 알림톡 전송", description = "카카오 알림톡을 전송 한다.")
    public int sendATS_one(@RequestBody AlarmTalkDto alarmTalkDto) {
        return commonService.sendKaKaoSend(alarmTalkDto);
    }

}
