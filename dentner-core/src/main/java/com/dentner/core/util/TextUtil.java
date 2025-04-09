package com.dentner.core.util;

import com.dentner.core.cmmn.vo.RequestApprovalVo;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;

public class TextUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    public static final String SHA256_ALGORITHM = "SHA256";
    public static final String HMAC_ALGORITHM = "HmacSHA256";
    public static String requestFormMakeText(RequestApprovalVo requestApprovalVo) {

        String content = "";
        // 간편
        if("A".equals(requestApprovalVo.getDetailList().get(0).getRequestSe())){
            content += "1. 의뢰인 아이디(이메일): " + requestApprovalVo.getClientEmail() +"\n";
            content += "2. 치자이너 아이디(이메일): " + requestApprovalVo.getDesignerEmail() +"\n";
            content += "3. 치자이너 이름: " + requestApprovalVo.getDesignerName() +"\n";
            content += "4. 치자이너 사업자명: " + requestApprovalVo.getMemberBusinessName() +"\n";
            content += "5. 매칭된날짜: " + requestApprovalVo.getEstimateDt() +"\n";
            content += "6. 의뢰번호: " + requestApprovalVo.getDetailList().get(0).getRequestNumber() +"\n";
            content += "7. 보철종류별 개수: " + requestApprovalVo.getRequestDocDesc() +"\n";
            content += "8. 가공방법: " + requestApprovalVo.getDetailList().get(0).getRequestProcessNoName() +"\n";
            content += "9. 상세설명: " + requestApprovalVo.getDetailList().get(0).getRequestDc() +"\n";
        }else{
            // 상세
            content += "1. 의뢰인 아이디(이메일): " + requestApprovalVo.getClientEmail() +"\n";
            content += "2. 치자이너 아이디(이메일): " + requestApprovalVo.getDesignerEmail() +"\n";
            content += "3. 치자이너 이름: " + requestApprovalVo.getDesignerName() +"\n";
            content += "4. 치자이너 사업자명: " + requestApprovalVo.getMemberBusinessName() +"\n";
            content += "5. 매칭된날짜: " + requestApprovalVo.getEstimateDt() +"\n";
            content += "6. 의뢰번호: " + requestApprovalVo.getDetailList().get(0).getRequestNumber() +"\n";
            content += "7. 보철종류별 개수: " + requestApprovalVo.getRequestDocDesc() +"\n";

            if(requestApprovalVo.getDetailList().size() > 1){
                for (int i = 0; i < requestApprovalVo.getDetailList().size(); i++) {
                    content += (i+1) +"번째 의뢰서 \n";
                    content += "8-"+(i+1)+". 가공방법: " + requestApprovalVo.getDetailList().get(i).getRequestProcessNoName() +"\n";
                    content += "9-"+(i+1)+". 상세설명: " + requestApprovalVo.getDetailList().get(i).getRequestDc() +"\n";
                    content += "10-"+(i+1)+". 임플란트 종류 입력: " + requestApprovalVo.getDetailList().get(i).getImplantType() +"\n";
                    content += "11-"+(i+1)+". 수치값: " + requestApprovalVo.getDetailList().get(i).getValueSj() +"\n";
                    content += "12-"+(i+1)+". pontic 디자인: " + requestApprovalVo.getDetailList().get(i).getRequestPonticSeName() +"\n";
                    content += "13-"+(i+1)+". 상세설명: " + requestApprovalVo.getDetailList().get(i).getRequestDc() +"\n";
                }
            }else{
                content += "8. 가공방법: " + requestApprovalVo.getDetailList().get(0).getRequestProcessNoName() +"\n";
                content += "9. 상세설명: " + requestApprovalVo.getDetailList().get(0).getRequestDc() +"\n";
                content += "10. 임플란트 종류 입력: " + requestApprovalVo.getDetailList().get(0).getImplantType() +"\n";
                content += "11. 수치값: " + requestApprovalVo.getDetailList().get(0).getValueSj() +"\n";
                content += "12. pontic 디자인: " + requestApprovalVo.getDetailList().get(0).getRequestPonticSeName() +"\n";
                content += "13. 상세설명: " + requestApprovalVo.getDetailList().get(0).getRequestDc() +"\n";
            }

        }
        String fileName = requestApprovalVo.getDetailList().get(0).getRequestBakNumber() + ".txt";
        String tempDir = System.getProperty("java.io.tmpdir");
        Path filePath = Path.of(tempDir, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            return "파일 생성 중 오류 발생: " + e.getMessage();
        }
        // 파일 경로 반환
        return filePath.toAbsolutePath().toString();
    }
    public static String generateTxId(int length) {
        StringBuilder txId = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            txId.append(CHARACTERS.charAt(index));
        }
        return txId.toString();
    }
    public static String getHmacSignature(String secret, String requestBody) throws UnsupportedEncodingException {
        byte[] key = secret.getBytes();
        byte[] message = requestBody.getBytes("utf-8");
        final SecretKeySpec secretKey = new SecretKeySpec(key, HMAC_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKey);
            return toBase64(mac.doFinal(message));
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return "";
        }
    }
    public static String toBase64(byte[] h) {
        return Base64.getEncoder().encodeToString(h);
    }
}
