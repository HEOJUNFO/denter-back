package com.dentner.core.util;

public class CalculateUtil {
    public static int calculateAmount(String unit, int amount, int exchangeRate) {
        int finalAmount = 0;

        if ("A".equals(unit)) {
            //finalAmount = (int) (amount - (amount * 0.0308)); // 소수점 이하 제거
            finalAmount = (int) (amount * 0.9692); // 소수점 이하 제거
        } else {
            finalAmount = (int) (amount * exchangeRate * 0.9); // 반올림하여 정수로 변환
        }

        //finalAmount = (finalAmount / 10) * 10;
        return finalAmount;
    }

    public static int calculateExchangeAmount(int amount, int exchangeRate) {
        int finalAmount = 0;
        finalAmount = (int) Math.round(amount * exchangeRate); // 반올림하여 정수로 변환

        //finalAmount = (finalAmount / 10) * 10;
        return finalAmount;
    }

    public static int calculateRefundAmount(String unit, int amount, int exchangeRate, String refundStatus, int refundAmount) {
        int finalAmount = 0;

        // 전체 환불된 경우
        if ("B".equals(refundStatus)) {
            amount = 0;
        }
        // 일부 환불된 경우
        else if ("C".equals(refundStatus)) {
            amount = refundAmount;
        }
        // 환불이 없는 경우
        if ("A".equals(unit)) {
            //finalAmount = (int) (amount - (amount * 0.0308)); // 소수점 이하 제거
            finalAmount = (int) (amount * 0.9692); // 소수점 이하 제거
        } else {
            finalAmount = (int) Math.round(amount * 0.9 * exchangeRate); // 반올림하여 정수로 변환
        }

        // 최종 금액을 10원 단위로 조정
        //finalAmount = (finalAmount / 10) * 10;
        return finalAmount;
    }
}
