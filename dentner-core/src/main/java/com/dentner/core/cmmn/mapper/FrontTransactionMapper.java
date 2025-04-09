package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface FrontTransactionMapper {
    List<RequestFormVo> selectTransactionList(RequestFormDto requestFormDto);

    int selectTransactionListCnt(RequestFormDto requestFormDto);

    List<RequestEstimateVo> selectTransactionEstimateList(RequestEstimateDto requestEstimateDto);

    int selectTransactionEstimateListCnt(RequestEstimateDto requestEstimateDto);

    int updateEstimateAllCancel(@Param("requestFormNo") Integer requestFormNo);

    int updateEstimateChoice(@Param("requestFormNo") Integer requestFormNo, @Param("targetNo")Integer targetNo);

    int updateRequestStatus(@Param("requestFormNo")Integer requestFormNo, @Param("status")String status, @Param("memberNo")Integer memberNo);

    int updateRequestDealStatus(@Param("requestFormNo")Integer requestFormNo, @Param("status")String status, @Param("memberNo")Integer memberNo);

    TransactionEstimateDetailVo selectTransactionEstimateDetail(@Param("requestEstimateNo")Integer requestEstimateNo, @Param("memberNo")Integer memberNo);

    void updateDesignerNo(@Param("requestFormNo") Integer requestFormNo, @Param("targetNo")Integer targetNo, @Param("memberNo")Integer memberNo);

    List<HashMap<String, Object>> selectEstimateProstheticsList(@Param("requestEstimateNo") Integer requestEstimateNo);

    String selectEstimateDocDesc(@Param("requestEstimateNo") Integer requestEstimateNo);

    TransactionPaymentVo selectTransactionPayment(@Param("requestFormNo") Integer requestFormNo ,@Param("memberNo")int memberNo);

    List<HashMap<String, Object>> selectPaymentProstheticsList(@Param("requestFormNo")Integer requestFormNo);

    String selectPaymentDocDesc(@Param("requestFormNo")Integer requestFormNo);

    List<Map<String, Object>> selectTransactionDoc(@Param("requestFormNo") Integer requestFormNo ,@Param("memberNo")int memberNo);

    Map<String, Object> selectTransactionContract(@Param("requestFormNo") Integer requestFormNo ,@Param("memberNo")int memberNo);

    List<RequestDocVo> selectTransactionDocDetail(@Param("requestDocGroupNo") Integer requestDocGroupNo, @Param("memberNo")int memberNo, @Param("memberSe")String memberSe);

    List<HashMap<String, Object>> selectDocProstheticsList(@Param("requestDocNo") Integer requestDocNo);

    int insertTransactionAddPay(TransactionCadAddDto transactionCadAddDto);

    TransactionEstimateDetailVo selectTransactionEstimateDetailDesigner(@Param("requestFormNo")Integer requestFormNo, @Param("memberNo")int memberNo);

    List<HashMap<String, Object>> selectEstimateDesignerProstheticsList(@Param("requestFormNo") Integer requestFormNo , @Param("memberNo")int memberNo);

    String selectEstimateDesignerDocDesc(@Param("requestFormNo") Integer requestFormNo , @Param("memberNo")int memberNo);

    int insertTransactionPay(MileageAddDto mileageAddDto);

    int updateRequestFormDc(MileageAddDto mileageAddDto);

    int insertTransactionRefuse(RequestFormContractDto requestFormContractDto);

    int updateTransactionAddPay(TransactionCadAddDto transactionCadAddDto);

    int updateTransactionDocReceive(@Param("requestFormNo") Integer requestFormNo , @Param("memberNo")int memberNo);

    RequestAddPayVo selectTransactionAddPay(@Param("requestFormNo") Integer requestFormNo);

    int insertTransactionRemaking(RequestRemakingAddDto requestRemakingAddDto);

    RequestRemakingVo selectTransactionRemaking(@Param("requestFormNo") Integer requestFormNo , @Param("memberNo")int memberNo);

    int updateRequest3dNext(@Param("requestFormNo") Integer requestFormNo , @Param("memberNo")int memberNo);

    List<RequestCadFileVo> selectTransactionCadFile(@Param("requestFormNo") Integer requestFormNo , @Param("memberNo")int memberNo,  @Param("memberSe")String memberSe);

    int insertReview(ReviewDto reviewDto);
    Map<String, Object> selectTransactionFormAddPay(@Param("requestFormNo") Integer requestFormNo ,@Param("memberNo")int memberNo, @Param("memberSe") String memberSe);

    Integer selectTransactionMileage(@Param("memberNo")int memberNo);

    int deleteTransactionAddPay(@Param("requestFormNo") Integer requestFormNo ,@Param("memberNo")int memberNo);

    int deleteTransactionRemaking(@Param("requestFormNo") Integer requestFormNo ,@Param("memberNo")int memberNo);

    TransactionStatVo selectTransactionStat(@Param("memberNo")int memberNo);

    RequestApprovalVo selectRequestApproval(@Param("requestFormNo") Integer requestFormNo ,@Param("memberNo")int memberNo);

    List<RequestApprovalDetailVo> selectRequestApprovalDetail(@Param("requestFormNo") Integer requestFormNo ,@Param("memberNo")int memberNo);

    int deleteTransactionCancel(RequestFormCancelDto requestFormCancelDto);

    int insertTransactionCancel(RequestFormCancelDto requestFormCancelDto);

    TransactionStatusVo selectTransactionStatus(@Param("requestFormNo") Integer requestFormNo ,@Param("memberNo")int memberNo);

    TransactionDataVo selectTransactionData(Integer requestFormNo);

    int updateRequestData(@Param("requestFormNo") Integer requestFormNo ,@Param("memberNo")int memberNo);

    int updateDocReceive(@Param("requestFormNo") Integer requestFormNo, @Param("memberNo") int memberNo);

    int updateRequest3dNextTarget(Integer requestFormNo, int memberNo);

    List<RequestFormVo> selectTransactionDesignerList(RequestFormDto requestFormDto);

    int selectTransactionDesignerListCnt(RequestFormDto requestFormDto);

    List<RequestTaskVo> selectTransactionTaskList();

    List<RequestTaskVo> selectTransactionDeadlineList();
    int updateRequestDeadline(@Param("requestFormNo")Integer requestFormNo, @Param("memberNo")Integer memberNo);

    List<RequestEstimateVo> selectTransactionTargetList(RequestEstimateDto requestEstimateDto);

    int insertTransactionRefundPay(MileageAddDto mileageAddDto);

    int insertTransactionCancelRefundPay(MileageAddDto mileageAddDto);

    int deleteTransactionHistory(@Param("memberSe")String memberSe, @Param("requestFormNo")Integer requestFormNo);

    int updateTransactionHistory(MileageAddDto mileageAddDto);

    void updateRemakingAddPay(@Param("requestFormNo")Integer requestFormNo, @Param("requestFormPayNo")Integer requestFormPayNo);

    ChatRoomAddDto selectTransactionChat(@Param("requestFormNo") Integer requestFormNo);

    String selectRequestCancelWhy(@Param("codeParentNo") Integer codeParentNo, @Param("codeNo") Integer codeNo);

    RequestFormVo selectRequestDealStatus(@Param("requestFormNo") Integer requestFormNo);

    int deleteTransactionEstimate(RequestFormCancelDto requestFormCancelDto);

    int insertTransaction3dInfo(Request3dInfoAdd request3dInfoAdd);

    List<ThreeInfoVO> selectTransaction3dInfo(@Param("requestFormNo") Integer requestFormNo);

    int deleteTransaction3dInfo(@Param("threeInfoNoArr")String threeInfoNoArr);

    void insert3dFile(S3FileVO fileVO);

    List<ThreeFileVO> selectFileList(@Param("threeInfoNo") Integer threeInfoNo);

    List<ThreeMemoVO> selectTransaction3dMemo(@Param("threeFileNo") Integer threeFileNo);

    int insertTransaction3dMemo(Request3dMemoAdd request3dMemoAdd);

    int deleteTransaction3dMemo(@Param("threeMemoNoArr")String threeMemoNoArr);

    void updateDesignerStatus(@Param("requestFormNo") Integer requestFormNo);

    Request3dMemoAdd select3dMemo(Request3dMemoAdd request3dMemoAdd);

    int insertTransactionCancelRefundAllPay(MileageAddDto mileageAddDto);

    int selectTransactionEstimateReceive(@Param("requestFormNo") Integer requestFormNo, @Param("memberNo") Integer memberNo);

    int selectTransactionReceive(@Param("requestFormNo") Integer requestFormNo);
}
