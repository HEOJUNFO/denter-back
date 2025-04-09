package com.dentner.front.api.common;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.mapper.FrontTransactionMapper;
import com.dentner.core.cmmn.service.FileService;
import com.dentner.core.cmmn.service.MailService;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.*;
import com.dentner.core.util.AlarmTalkEnum;
import com.dentner.core.util.ConstUtil;
import com.dentner.core.util.EmailUtil;
import com.dentner.front.api.file.S3DownloadService;
import com.dentner.front.version.V1ApiVersion;
import com.popbill.api.AccountCheckInfo;
import com.popbill.api.AccountCheckService;
import com.popbill.api.PopbillException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequiredArgsConstructor
@Tag(name = "공통 api", description = "공통 API")
public class CommonController implements V1ApiVersion {

    @Autowired
    FrontTransactionMapper frontTransactionMapper;

    @Resource(name= "commonService")
    CommonService commonService;

    @Autowired
    private MailService mailService;

    @Resource(name= "fileService")
    FileService fileService;

    private final S3DownloadService s3DownloadService;


    @Autowired
    private AccountCheckService accountCheckService;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client s3Client;

    // 팝빌회원 사업자번호
    @Value("${popbill.corpNum}")
    private String CorpNum;

    @GetMapping({"/common/code", "/common/code/{parentNo}"})
    @ResponseMessage("코드 목록 조회 성공")
    @Operation(summary = "코드 목록", description = "코드 목록을 조회한다.")
    @Parameter(name = "parentNo", description = "코드 상위 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public List<CodeVo> getCodeList(@PathVariable(required = false) Integer parentNo, @RequestParam String type) {
        return commonService.getCodeList(parentNo, type);
    }

    @PostMapping("/common/mail")
    @ResponseMessage("메일 발송 성공")
    @Operation(summary = "메일 발송 등록", description = "메일을 발송한다.")
    @Parameters({
            @Parameter(name = "mailTo", description = "메일 주소", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "mailSubject", description = "메일 제목", example = "" ,  schema = @Schema(type = "string")),
            @Parameter(name = "mailContent", description = "메일 내용", example = "" ,  schema = @Schema(type = "string"))
    })
    public MailDto postMail(@ModelAttribute MailDto mailDto){
        return commonService.postMail(mailDto);
    }
    
    /**
     * 계좌인증
     * @param mailDto
     * @return
     * @throws Exception 
     */
    @PostMapping("/common/acc-check")
    public String postAccCheck(@RequestBody MemberBankDto memberBankDto) throws Exception{
        return commonService.postAccCheck(memberBankDto);
    }

    @PostMapping("/common/edit/file")
    @ResponseMessage("에디트 이미지 등록 성공")
    @Operation(summary = "에디트 이미지 등록", description = "에디트 이미지를 등록한다.")
    public FileVO postEditFile(@RequestBody FileDto fileDto) throws IOException {
        return commonService.postEditFile(fileDto);
    }

    @PostMapping("/common/download/{fileNo}")
    @Operation(summary = "파일다운로드", description = "파일을 다운로드 한다.")
    @Parameter(name = "fileNo", description = "파일 NO", example = "" ,  schema = @Schema(type = "integer", format = "int32"))
    public ResponseEntity<StreamingResponseBody> getFileDownload(@PathVariable("fileNo") Integer fileNo, HttpServletRequest req) throws Exception {
        S3FileVO fileVO = fileService.selectFile(fileNo);
        String fileKey = fileVO.getFileName();
        String downloadFileName = fileVO.getFileRealName();

        return s3DownloadService.getObject(fileKey, downloadFileName, req);
    }

    @PostMapping("/common/download/chat/{chatNo}")
    @Operation(summary = "채팅파일 다운로드", description = "채팅 파일을 다운로드 한다.")
    @Parameter(name = "chatNo", description = "파일 NO", example = "" ,  schema = @Schema(type = "integer", format = "int32"))
    public ResponseEntity<StreamingResponseBody> getChatFileDownload(@PathVariable("chatNo") Integer chatNo, HttpServletRequest req) throws Exception {
        S3FileVO fileVO = fileService.selectChatFile(chatNo);
        String fileKey = fileVO.getFileName();
        String downloadFileName = fileVO.getFileRealName();

        return s3DownloadService.getObject(fileKey, downloadFileName, req);
    }

    @PostMapping("/common/download/encrypt/{fileNo}")
    @Operation(summary = "암호화 파일다운로드", description = "암호화 파일을 다운로드 한다.")
    @Parameter(name = "fileNo", description = "파일 NO", example = "" ,  schema = @Schema(type = "integer", format = "int32"))
    public ResponseEntity<ByteArrayResource> getEncryptFileDownload(@PathVariable("fileNo") Integer fileNo, HttpServletRequest req) throws Exception {
        S3FileVO fileVO = fileService.selectFile(fileNo);
        String fileKey = fileVO.getFileName();
        String downloadFileName = fileVO.getFileRealName();

        return s3DownloadService.getObjectEncrypt(fileKey, downloadFileName, req);
    }

    @GetMapping("/common/public/download/{fileName}")
    @Operation(summary = "파일다운로드", description = "파일을 다운로드 한다.")
    @Parameter(name = "fileName", description = "파일 명", example = "", schema = @Schema(type = "string"))
    public ResponseEntity<StreamingResponseBody> getFilePublicDownload(@PathVariable("fileName") String fileName, HttpServletRequest req) throws Exception {
        fileName = "baseForm/"+ fileName;
        return s3DownloadService.getObject(fileName, fileName, req);
    }

    @GetMapping("/common/status")
    @ResponseMessage("health 체크 성공")
    @Operation(summary = "health 체크", description = "health 체크를 한다..")
    public int getStatus() {
        return 1;
    }

    @PostMapping("/common/auth-phone")
    @ResponseMessage("SMS 발송 성공")
    @Operation(summary = "SMS 발송 등록", description = "SMS를 발송한다.")
    @Parameters({
            @Parameter(name = "phone", description = "핸드폰번호", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "email", description = "이메일", example = "", schema = @Schema(type = "string")),
            @Parameter(name = "memberContactNation", description = "전화번호 코드 NO", example = "" ,  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "certification", description = "인증 확인 여부(A,B,C)", example = "", schema = @Schema(type = "string"))
    })

    public AuthCodeDto postAuthPhone(@RequestBody PhoneDto phoneDto) throws Exception{
        return commonService.postAuthPhone(phoneDto);
    }

    @GetMapping("/common/auth-phone")
    @ResponseMessage("SMS 인증")
    @Operation(summary = "SMS 인증", description = "SMS 인증번호를 확인한다.")
    @Parameters({
            @Parameter(name = "authCode", description = "인증번호", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "token", description = "토큰", example = "",  schema = @Schema(type = "string"))
    })
    public int getAuthPhone(@RequestParam String authCode, String token) throws Exception{
        return commonService.getAuthPhone(authCode, token);
    }

    @GetMapping({"/common/teeth-type", "/common/teeth-type/{parentNo}"})
    @ResponseMessage("보철종류 코드 성공")
    @Operation(summary = "보철종류 코드 조회", description = "보철종류 코드 조회한다.")
    @Parameter(name = "parentNo", description = "코드 상위 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public List<TeethTypeVo> getTeethType(@PathVariable(required = false) Integer parentNo, @RequestParam String type) {
        return commonService.getTeethType(parentNo, type);
    }

    @GetMapping("/common/alarm-cnt")
    @ResponseMessage("알림 개수 조회")
    @Operation(summary = "알림 개수 조회", description = "알림 개수 조회한다.")
    public int getAlarmCnt(){
        return commonService.getAlarmCnt();
    }

    @GetMapping("/common/alarm")
    @ResponseMessage("알림 목록 조회 성공")
    @Operation(summary = "알림 목록 정보 조회", description = "알림 목록 정보를 조회한다.")
    @Parameters({
            @Parameter(name = "startRow", description = "페이징 시작번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "pageCnt", description = "페이징 카운트", example = "",  schema = @Schema(type = "integer", format = "int32"))
    })
    public AlarmListVo getAlarmList(@ModelAttribute AlarmDto alarmDto) {
        return commonService.getAlarmList(alarmDto);
    }

    @PutMapping("/common/alarm/read-all/{type}")
    @ResponseMessage("알림 전체 읽음처리 성공")
    @Operation(summary = "알림 전체 읽음처리", description = "알림을 전체 읽음처리 한다.")
    @Parameter(name = "type", description = "알림타입(A:전체,B:덴트너소식)", example = "",  schema = @Schema(type = "string"))
    public int putAlarmReadAll(@PathVariable(required = false) String type){
        return commonService.putAlarmReadAll(type);
    }

    @PutMapping("/common/alarm/read/{alarmNo}")
    @ResponseMessage("알림 읽음처리 성공")
    @Operation(summary = "알림 읽음처리", description = "알림을 읽음처리 한다.")
    @Parameter(name = "alarmNo", description = "알림 NO", example = "" ,  schema = @Schema(type = "integer", format = "int32"))
    public int putAlarmRead(@PathVariable("alarmNo") Integer alarmNo){
        return commonService.putAlarmRead(alarmNo);
    }

    @PostMapping( "/common/kakao/send")
    @ResponseMessage("카카오 알림톡 전송")
    @Operation(summary = "카카오 알림톡 전송", description = "카카오 알림톡을 전송 한다.")
    @Parameters({
            @Parameter(name = "templateCode", description = "템플릿 코드", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "content", description = "내용", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "receiverNum", description = "수신 전화번호", example = "",  schema = @Schema(type = "string"))
    })
    public int sendATS_one(@RequestBody AlarmTalkDto alarmTalkDto) {
        return commonService.sendKaKaoSend(alarmTalkDto);
    }

    @PostMapping("/common/download/zip/{fileSe}/{fileFormNo}")
    @Operation(summary = "파일 ZIP 다운로드", description = "여러 파일을 ZIP 파일로 다운로드한다.")
    @Parameters({
            @Parameter(name = "fileSe", description = "파일구분", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "fileFormNo", description = "각 파일 타입별 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    })
    public ResponseEntity<ByteArrayResource> downloadFilesAsZip(@PathVariable(required = false) String fileSe,
                                                                @PathVariable(required = false) Integer fileFormNo) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        List<S3FileVO> fileList = fileService.selectFiles(fileFormNo, fileSe);
        for (S3FileVO fileVO : fileList) {
            String fileKey = fileVO.getFileName();
            String downloadFileName = fileVO.getFileRealName();

            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucket, fileKey));
            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(objectInputStream);

            ZipEntry zipEntry = new ZipEntry(downloadFileName);
            zipEntry.setSize(bytes.length);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(bytes);
            zipOutputStream.closeEntry();
            objectInputStream.close();
        }
        zipOutputStream.close();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formatedNow = now.format(formatter);
        String zipFileName = formatedNow + "_files.zip";


        // 응답 헤더 설정
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(byteArrayOutputStream.size());
        httpHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(zipFileName).build());

        // ZIP 파일 리소스를 응답으로 반환
        ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .body(resource);
    }

    @PostMapping( "/common/account")
    @ResponseMessage("예금주 조회")
    @Operation(summary = "예금주 조회", description = "예금주를 조회 한다.")
    @Parameters({
            @Parameter(name = "memberAccountBankNo", description = "기관코드", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberAccountNumber", description = "계좌번호", example = "",  schema = @Schema(type = "string"))
    })
    public int checkDepositorInfo(@RequestBody BankDto bankDto) {
        int result = 0;
        /**
         * 1건의 예금주성명을 조회합니다.
         * - https://developers.popbill.com/reference/accountcheck/java/api/check#CheckAccountInfo
         */

        // 조회할 기관코드
        String BankCode = bankDto.getMemberAccountBankNo();
        // 조회할 기관의 계좌번호 (하이픈 '-' 제외 8자리 이상 14자리 이하)
        String AccountNumber = bankDto.getMemberAccountNumber();
        AccountNumber = AccountNumber.replaceAll("-","");

        try {

            AccountCheckInfo accountInfo = accountCheckService.CheckAccountInfo(CorpNum, BankCode, AccountNumber);
            //System.out.println("accountInfo1 = " + accountInfo.getAccountName());
            //System.out.println("accountInfo2 = " + accountInfo.getAccountNumber());
            //System.out.println("accountInfo3 = " + accountInfo.getCheckDate());
            //System.out.println("accountInfo4 = " + accountInfo.getResult());
            //System.out.println("accountInfo5 = " + accountInfo.getBankCode());
            //System.out.println("accountInfo6 = " + accountInfo.getResultCode());
            //System.out.println("accountInfo7 = " + accountInfo.getResultMessage());

            if("0000".equals(accountInfo.getResultCode())){
                if(bankDto.getMemberAccountName().equals(accountInfo.getAccountName())){
                    result = 1;
                }else{
                    result = 0;
                }
            }else{
                result = 0;
            }
        } catch (PopbillException e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }

    @PostMapping("/common/download/zip/encrypt/{fileSe}/{fileFormNo}")
    @Operation(summary = "암호화 파일 ZIP 다운로드", description = "여러 암호화 파일을 ZIP 파일로 다운로드한다.")
    @Parameters({
            @Parameter(name = "fileSe", description = "파일구분", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "fileFormNo", description = "각 파일 타입별 NO", example = "",  schema = @Schema(type = "integer", format = "int32"))
    })
    public ResponseEntity<ByteArrayResource> downloadFilesAsEncryptZip(@PathVariable(required = false) String fileSe,
                                                                       @PathVariable(required = false) Integer fileFormNo,
                                                                       HttpServletRequest req) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        List<S3FileVO> fileList = fileService.selectFiles(fileFormNo, fileSe);
        for (S3FileVO fileVO : fileList) {
            String fileKey = fileVO.getFileName();
            String downloadFileName = fileVO.getFileRealName();

            // 파일 복호화 처리
            ResponseEntity<ByteArrayResource> response = s3DownloadService.getObjectEncrypt(fileKey, downloadFileName, req);
            ByteArrayResource resource = response.getBody();
            byte[] decryptedFile = resource.getByteArray();  // 복호화된 파일 내용

            // ZIP 파일에 복호화된 파일 추가
            ZipEntry zipEntry = new ZipEntry(downloadFileName);
            zipEntry.setSize(decryptedFile.length);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(decryptedFile);
            zipOutputStream.closeEntry();
        }
        zipOutputStream.close();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formatedNow = now.format(formatter);
        String zipFileName = formatedNow + "_files.zip";


        // 응답 헤더 설정
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(byteArrayOutputStream.size());
        httpHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(zipFileName).build());

        // ZIP 파일 리소스를 응답으로 반환
        ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .body(resource);
    }

    @PostMapping("/common/file/upload")
    @ResponseMessage("파일 업로드 성공")
    @Operation(summary = "파일 업로드", description = "파일을 업로드 한다.")
    public S3FileVO postFile(@RequestParam(required = false) MultipartFile file) throws Exception{
        return commonService.postFile(file);
    }

    @GetMapping("/common/stat-cnt")
    @ResponseMessage("메인 통계 데이터 조회 성공")
    @Operation(summary = "메인 통계 데이터", description = "메인 통계 데이터를 조회한다.")
    public MainStatVo getMainStatData() {
        return commonService.getMainStatData();
    }

    @PostMapping("/common/push")
    @ResponseMessage("푸시 발송 성공")
    @Operation(summary = "푸시 발송", description = "푸시를 발송한다.")
    public int postPush(@RequestBody PushDto pushDto) throws Exception{
        int result = 1;
        commonService.postPush(pushDto);
        return result;
    }

    @GetMapping("/common/test")
    @ResponseMessage("메인 통계 데이터 조회 성공")
    @Operation(summary = "메인 통계 데이터", description = "메인 통계 데이터를 조회한다.")
    public void test() throws Exception{
        List<RequestTaskVo> requestTaskVoList = frontTransactionMapper.selectTransactionDeadlineList();
        if(requestTaskVoList != null && requestTaskVoList.size() > 0){
            // 2. 납품 마감에 따른 납품여부 값을 변경.
            for (int i = 0; i < requestTaskVoList.size(); i++) {
                RequestTaskVo requestTaskVo = requestTaskVoList.get(i);
                // 알림톡을 보낸다.
                AlarmTalkDto alarmTalkDto = new AlarmTalkDto();
                alarmTalkDto = commonService.selectRequestInfo(requestTaskVo.getRequestFormNo());

                int cnt = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 17);
                if (cnt > 0) { // 알림이 켜져 있음.
                    if("A".equals(alarmTalkDto.getMemberTp())) {    // 국내 의뢰인인 경우 카카오톡 전송
                        alarmTalkDto.setTemplateCode(AlarmTalkEnum.PROJECT_COMPLETION_AND_PAYMENT_PROCESSING.getCode());
                        alarmTalkDto.setReceiverNum(alarmTalkDto.getRequestHp());
                        String content = AlarmTalkEnum.PROJECT_COMPLETION_AND_PAYMENT_PROCESSING.getMessageTemplate();
                        String message = content.replace("#{치자이너}", alarmTalkDto.getDesignerNickName())
                                .replace("#{프로젝트}", alarmTalkDto.getRequestFormSj());
                        alarmTalkDto.setContent(message);
                        commonService.sendKaKaoSend(alarmTalkDto);
                    }else{
                        // 이메일 발송
                        MailDto mailDto = new MailDto();
                        String emailTemplate = EmailUtil.readDeadlineHTMLTemplate(alarmTalkDto);

                        mailDto.setMailTo(alarmTalkDto.getRequestEmail());
                        mailDto.setMailSubject("[Dentner]Deadline passed");
                        mailDto.setMailContent(emailTemplate);
                        mailService.mailSend(mailDto);
                    }
                }

                if("B".equals(alarmTalkDto.getRequestFormSe())){   // 지정 요청
                    int cnt1 = commonService.selectAlarm(Integer.parseInt(alarmTalkDto.getRegisterNo()), 4);
                    if (cnt1 > 0) {
                        // 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
                        // 회원유형 (A:한국인, B:외국인)
                        String memberTp = requestTaskVo.getMemberTp();
                        String sj = "";
                        String cn = "";
                        if("A".equals(memberTp)){
                            sj = "납품 마감";
                            cn = ConstUtil.REQUEST_TARGET_MSG3;
                        }else{
                            sj = "Deadline";
                            cn = ConstUtil.REQUEST_TARGET_ENG_MSG3;
                        }
                        // 납품마감이 지났을 경우 알림 보내기
                        AlarmAddDto alarmAddDto = new AlarmAddDto();
                        alarmAddDto.setAlarmSj(sj);
                        String message1 = cn;
                        alarmAddDto.setAlarmCn(message1);
                        alarmAddDto.setAlarmSe("D");
                        alarmAddDto.setAlarmUrl(requestTaskVo.getRequestFormNo().toString());
                        alarmAddDto.setMemberNo(Integer.parseInt(alarmTalkDto.getRegisterNo()));
                        commonService.postAlarm(alarmAddDto);
                    }
                }

                frontTransactionMapper.updateRequestDeadline(requestTaskVo.getRequestFormNo(), 0);
            }
        }
    }
}
