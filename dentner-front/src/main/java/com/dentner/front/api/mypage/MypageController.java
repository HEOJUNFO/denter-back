package com.dentner.front.api.mypage;


import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.service.response.ResponseMessage;
import com.dentner.core.cmmn.vo.*;
import com.dentner.front.version.V1ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "마이페이지 관리", description = "마이페이지 API")
public class MypageController implements V1ApiVersion {

	@Resource(name= "mypageService")
    MypageService mypageService;

    @GetMapping("/mypage/profile")
    @ResponseMessage("프로필 정보 조회 성공")
    @Operation(summary = "프로필 정보 조회", description = "프로필 정보를 조회한다.")
    public MemberProfileVo getMypageProfile() {
        return mypageService.getMypageProfile();
    }

    @PutMapping("/mypage/profile")
    @ResponseMessage("프로필 정보 수정 성공")
    @Operation(summary = "프로필 정보 수정", description = "프로필 정보를 수정한다.")
    @Parameters({
            @Parameter(name = "memberSe", description = "회원 구분", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberNickName", description = "닉네임", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "oneIntroduction", description = "한줄 소개", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberAreaNo", description = "지역 코드", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "fixProstheticsNo", description = "고정성 보철물 코드", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "removableProstheticsNo", description = "가철성 보철물 코드", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "correctNo", description = "교정 코드", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "allOnNo", description = "AllOnX 코드", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "establishYear", description = "설립연도", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "employeeCnt", description = "치과기공사 수", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "modifyCnt", description = "수정가능횟수", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "modifyWarrantyDay", description = "수정보증기간", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "aboutUs", description = "회사 소개", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "showAt", description = "공개 여부", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "fileDel", description = "파일삭제 No (콤마로 구분)", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "note", description = "참고사항", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "swNo", description = "소프트웨어 No (콤마로 구분)", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "swEtc", description = "소프트웨어 기타 정보", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "photo", description = "프로필 이미지", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "typeList", description = "치자이너 보철수가", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "images", description = "회사사진 or 포토폴리오 이미지", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "files", description = "3d 포토폴리오 파일", example = "",  schema = @Schema(type = "string"))
    })
    public MemberProfileAddDto putMypageProfile(@RequestParam(required = false) MultipartFile photo,
                                                @RequestParam(required = false) List<MultipartFile> images,
                                                @RequestParam(required = false) List<MultipartFile> files,
                                                @ModelAttribute MemberProfileAddDto memberProfileAddDto) throws Exception{
        return mypageService.putMypageProfile(photo, images, files, memberProfileAddDto);
    }

    @PutMapping("/mypage/out")
    @ResponseMessage("회원탈퇴 성공")
    @Operation(summary = "회원탈퇴", description = "회원을 탈퇴한다.")
    public int putMypageOut(){
        return mypageService.putMypageOut();
    }

    @GetMapping("/mypage/interest/{interestSe}")
    @ResponseMessage("관심 목록 조회 성공")
    @Operation(summary = "관심 목록 정보 조회", description = "관심 목록 정보를 조회한다.")
    @Parameter(name = "interestSe", description = "관심대상 구분 (A:치과기공소, B:치자이너)", example = "",  schema = @Schema(type = "string"))
    public InterestListVo getInterestList(@PathVariable("interestSe") String interestSe, @ModelAttribute InterestDto interestDto) {
        return mypageService.getInterestList(interestSe, interestDto);
    }

    @GetMapping("/mypage/block/{blockSe}")
    @ResponseMessage("차단 목록 조회 성공")
    @Operation(summary = "차단 목록 정보 조회", description = "차단 목록 정보를 조회한다.")
    @Parameter(name = "blockSe", description = "차단대상 구분 (A:치과기공소, B:치자이너)", example = "",  schema = @Schema(type = "string"))
    public BlockListVo getBlockList(@PathVariable("blockSe") String blockSe, @ModelAttribute BlockDto blockDto) {
        return mypageService.getBlockList(blockSe, blockDto);
    }

    @PutMapping("/mypage/type")
    @ResponseMessage("보철 수가 저장 성공")
    @Operation(summary = "보철 수가 저장", description = "보철 수가를 저장한다.")
    @Parameters({
            @Parameter(name = "memberFirstValue", description = "보철 FIRST 코드", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberMiddleValue", description = "보철 MIDDLE 코드", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "typeAmount", description = "보철금액", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "amountType", description = "보철구분", example = "",  schema = @Schema(type = "string"))
    })
    public int putMypageType(@RequestBody MemberTypeDto memberTypeDto){
        return mypageService.putMypageType(memberTypeDto);
    }

    @GetMapping("/mypage/type-list")
    @ResponseMessage("보철 수가 정보 조회 성공")
    @Operation(summary = "보철 수가 정보 조회", description = "보철 수가 정보를 조회한다.")
    public List<MemberTypeVo> getMypageTypeList() {
        return mypageService.getMypageTypeList();
    }

    @GetMapping("/mypage/review")
    @ResponseMessage("작성한 리뷰 목록 조회 성공")
    @Operation(summary = "작성한 리뷰 목록 정보 조회", description = "작성한 리뷰 목록 정보를 조회한다.")
    @Parameter(name = "reviewSe", description = "리뷰 구분 (A:작성한 리뷰, B:받은 리뷰)", example = "",  schema = @Schema(type = "string"))
    public ReviewListVo getReviewList(@ModelAttribute ReviewDto reviewDto) {
        return mypageService.getReviewList(reviewDto);
    }

    @DeleteMapping("/mypage/review/{reviewNo}")
    @ResponseMessage("리뷰 삭제 성공")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제한다.")
    @Parameter(name = "reviewNo", description = "리뷰 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public int deleteMypageReview(@PathVariable("reviewNo") Integer reviewNo){
        return mypageService.deleteMypageReview(reviewNo);
    }

    @PutMapping("/mypage/review/{reviewNo}")
    @ResponseMessage("리뷰 수정 성공")
    @Operation(summary = "리뷰수정", description = "리뷰를 수정 한다.")
    @Parameters({
            @Parameter(name = "reviewNo", description = "리뷰 번호", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "reviewRate", description = "리뷰 별점", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "reviewCn", description = "리뷰 내용", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "images", description = "리뷰 사진", example = "",  schema = @Schema(type = "string"))
    })
    public int putReview(@PathVariable("reviewNo") Integer reviewNo,
                          @RequestParam(required = false) List<MultipartFile> images,
                          @ModelAttribute ReviewDto reviewDto){
        return mypageService.putReview(reviewNo, images, reviewDto);
    }

    @GetMapping("/mypage/review/{reviewNo}")
    @ResponseMessage("작성한 리뷰 상세 조회 성공")
    @Operation(summary = "작성한 리뷰 상세 정보 조회", description = "작성한 리뷰 상세 정보를 조회한다.")
    @Parameter(name = "reviewNo", description = "리뷰 번호", example = "",  schema = @Schema(type = "integer", format = "int32"))
    public ReviewVo getReviewDetail(@PathVariable("reviewNo") Integer reviewNo) {
        return mypageService.getReviewDetail(reviewNo);
    }

    @PutMapping("/mypage/phone")
    @ResponseMessage("전화번호 수정 성공")
    @Operation(summary = "전화번호 수정", description = "전화번호를 수정한다.")
    @Parameters({
            @Parameter(name = "memberContactNation", description = "전화번호 코드", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "phone", description = "전화번호", example = "",  schema = @Schema(type = "string"))
    })
    public int putMypagePhone(@RequestBody PhoneDto phoneDto) throws Exception {
        return mypageService.putMypagePhone(phoneDto);
    }

    @PutMapping("/mypage/password")
    @ResponseMessage("비밀번호 수정 성공")
    @Operation(summary = "비밀번호 수정", description = "비밀번호를 수정한다.")
    @Parameter(name = "password", description = "비밀번호", example = "",  schema = @Schema(type = "string"))
    public int putMypagePassword(@RequestBody PasswordDto passwordDto){
        return mypageService.putMypagePassword(passwordDto);
    }

    @GetMapping("/mypage/info")
    @ResponseMessage("개인 정보 조회 성공")
    @Operation(summary = "개인 정보 조회", description = "개인 정보를 조회한다.")
    public MemberVo getMypageInfo() {
        return mypageService.getMypageInfo();
    }

    @PutMapping("/mypage/info")
    @ResponseMessage("개인 정보 수정 성공")
    @Operation(summary = "개인 정보 수정", description = "개인 정보를 수정한다.")
    @Parameters({
            @Parameter(name = "memberDentistryName", description = "치과명", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberBusinessNumber", description = "사업자등록번호", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberAddress", description = "사업자주소", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberDetailAddress", description = "사업자주소 상세", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "memberAlarmAt", description = "알림톡 사용 여부", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberAlarmSe", description = "알림톡 구분", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberJobSe", description = "직업 구분", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberBusinessName", description = "사업자 명", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "memberTimezoneNo", description = "타임존 코드", example = "",  schema = @Schema(type = "integer", format = "int32")),
            @Parameter(name = "businessFile", description = "사업자등록증 파일", example = "",  schema = @Schema(type = "string"))
    })
    public MemberAddDto putMypageInfo(@RequestParam(required = false) MultipartFile businessFile,
                                                @ModelAttribute MemberAddDto memberAddDto) throws Exception{
        return mypageService.putMypageInfo(businessFile, memberAddDto);
    }

    @PostMapping("/mypage/profile/designer")
    @ResponseMessage("치자이너 프로필 등록 성공")
    @Operation(summary = "치자이너 프로필 등록", description = "치자이너 프로필을 등록한다.")
    public int postProfileDesigner() throws Exception{
        return mypageService.postProfileDesigner();
    }

    @PutMapping("/mypage/profile/change")
    @ResponseMessage("프로필 전환 성공")
    @Operation(summary = "프로필 전환", description = "프로필을 전환한다.")
    public TokenDto putProfileChange() {
        return mypageService.putProfileChange();
    }

    @GetMapping("/mypage/alarm")
    @ResponseMessage("알림 목록 조회 성공")
    @Operation(summary = "알림 목록 조회", description = "알림 목록을 조회한다.")
    public List<AlarmCodeVo> getAlarmList(@RequestParam String type) {
        return mypageService.getAlarmList(type);
    }

    @PutMapping("/mypage/alarm/{code}/{type}")
    @ResponseMessage("알림 on/off 전환 성공")
    @Operation(summary = "알림 on/off 전환", description = "알림 on/off 전환한다.")
    @Parameters({
        @Parameter(name = "code", description = "알림 코드", example = "",  schema = @Schema(type = "string")),
        @Parameter(name = "type", description = "알림 타입", example = "",  schema = @Schema(type = "string"))
    })
    public int putAlarm(@PathVariable("code") String code, @PathVariable("type") String type) {
        return mypageService.putAlarm(code, type);
    }

    @PutMapping("/mypage/alarm/all/{code}/{type}")
    @ResponseMessage("알림 all on/off 전환 성공")
    @Operation(summary = "알림 all on/off 전환", description = "알림 all on/off 전환한다.")
    @Parameters({
            @Parameter(name = "code", description = "알림 코드", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "type", description = "알림 타입 Y/N", example = "",  schema = @Schema(type = "string"))
    })
    public int putAllAlarm(@PathVariable("code") String code, @PathVariable("type") String type) {
        return mypageService.putAllAlarm(code, type);
    }

    @PutMapping("/mypage/profile/show/{type}/{memberSe}")
    @ResponseMessage("프로필 공개/비공개 전환 성공")
    @Operation(summary = "프로필 공개/비공개 전환", description = "프로필 공개/비공개 전환한다.")
    @Parameters({
            @Parameter(name = "memberSe", description = "회원 구분", example = "",  schema = @Schema(type = "string")),
            @Parameter(name = "type", description = "프로필 구분", example = "",  schema = @Schema(type = "string"))
    })
    public int putShow(@PathVariable("type") String type, @PathVariable("memberSe") String memberSe) {
        return mypageService.putShow(type, memberSe);
    }
    @GetMapping("/mypage/review-list/{memberNo}")
    @ResponseMessage("작성된 리뷰 목록 조회 성공")
    @Operation(summary = "작성된 리뷰 목록 정보 조회", description = "작성된 리뷰 목록 정보를 조회한다.")
    @Parameter(name = "memberNo", description = "회원 NO", example = "",  schema = @Schema(type = "string"))
    public ReviewListVo getReviewMemberList(@ModelAttribute ReviewDto reviewDto, @PathVariable("memberNo") Integer memberNo) {
        return mypageService.getReviewMemberList(reviewDto, memberNo);
    }
}
