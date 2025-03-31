package com.dentner.core.util;

import java.util.Map;

public enum AlarmTalkEnum {
    PROJECT_CREATION("023090000314", "#{의뢰인}님이 #{관리자}님에게 의뢰를 요청했습니다."),
    PROPOSAL_REQUEST("023090000315", "#{의뢰인}님이 #{치자이너}님에게 의뢰를 요청했습니다."),
    PAYMENT_RECEIVED("024090000700", "#{의뢰자}님이 작성하신 #{project_name} 댓글에 #{comment_user_name}님의 답글이 달렸습니다. \n" +
            "\n" +
            "해당 댓글 등록 메시지는 고객님의 알림신청에 의해 발송되었습니다"),
    PROJECT_CONTRACT_DOCUMENT_CREATION("024090000701", "요청하신 #{project_name}에 대한 #{치자이너}님의 견적서가 도착하였습니다.\n" +
            "\n" +
            "해당 견적서 도착 알림 메시지는 \n" +
            "고객님의 알림 신청에 의해 발송됩니다."),
    PROJECT_INITIATION("024090000696", "요청하신 회원가입이 \n" + "승인 완료되었습니다."),
    PROJECT_CANCELLATION("024090000698", "요청하신 회원가입이 \n" + "승인 거절되었습니다.\n" + "이메일을 확인해주세요."),
    PROJECT_PAYMENT("024090000699", "#{의뢰인}님이 작성하신 #{project_name}에 새로운 댓글이 달렸습니다. \n" +
            "\n" +
            "해당 새로운 댓글 알림 메시지는 고객님의 알림 신청에 의해 발송됩니다."),
    PROJECT_PROGRESS_REPORT_SUBMISSION("024090000706", "요청하신 #{project_name} 재제작에 대한 #{치자이너}님이 CAD File을 \n" +
            "업로드 했습니다.\n" +
            "\n" +
            "해당 알림 메시지는 \n" +
            "고객님의 알림 신청에 의해 발송됩니다."),
    PROJECT_PROGRESS_REPORT_REVISION("024090000708", "요청하신 #{project_name}에 대한 #{치자이너}님이 CAD File을 업로드 했습니다.\n" +
            "#{치자이너}님이 추가금을 요청했습니다.\n" +
            "\n" +
            "해당 알림 메시지는 \n" +
            "고객님의 알림 신청에 의해 발송됩니다."),
    PROJECT_PROGRESS_REPORT_COMPLETION("024090000710", "요청하신 #{project_name}에 대한 #{치자이너}님이 CAD File을 업로드 했습니다.\n" +
            "\n" +
            "해당 알림 메시지는 \n" +
            "고객님의 알림 신청에 의해 발송됩니다."),
    PROJECT_COMPLETION_DOCUMENT_SUBMISSION("024090000711", "요청하신 #{project_name}에 대한 #{치자이너}님이 의뢰서를 수령했습니다.\n" +
            "\n" +
            "해당 알림 메시지는 고객님의 알림 신청에 의해 발송됩니다."),
    PROJECT_PROGRESS_REPORT_REVISION_WITH_ADDITIONAL_PAYMENT("024090000712", "요청하신 #{project_name} 재제작에 대한 #{치자이너}님이 CAD File을 업로드 했습니다.\n" +
            "#{치자이너}님이 추가금을 요청했습니다.\n" +
            "\n" +
            "해당 알림 메시지는 \n" +
            "고객님의 알림 신청에 의해 발송됩니다."),
    PROJECT_REVISION_REQUEST("024090000713", "의뢰하신 #{프로젝트}가 취소되었습니다.\n" +
            "\n" +
            "해당 알림 메시지는 \n" +
            "고객님의 알림 신청에 의해 발송됩니다."),
    PROJECT_COMPLETION_CONFIRMATION("024090000714", "의뢰하신 #{프로젝트}가 견적이 마감되어 취소되었습니다.\n" +
            "\n" +
            "해당 알림 메시지는 \n" +
            "고객님의 알림 신청에 의해 발송됩니다."),
    PROJECT_COMPLETION_AND_PAYMENT_PROCESSING("024090000715", "의뢰하신 #{프로젝트}의 납품 시간이 지났습니다.\n" +
            "#{치자이너}와 소통해주세요.\n" +
            "\n" +
            "해당 알림 메시지는 \n" +
            "고객님의 알림 신청에 의해 발송됩니다."),
    ARCHITECT_COMMENT_ON_REQUEST("024090000718", "#{치과기공소or치자이너}님이 작성하신 #{project_name} 댓글에 답글이 달렸습니다. \n" +
            "\n" +
            "해당 새로운 댓글 알림 메시지는 고객님의 알림 신청에 의해 발송됩니다."),
    ARCHITECT_PROPOSAL_SUBMISSION("024090000726", "#{의뢰인}님의  #{project_name}에 제출하신 견적서가 매칭되었습니다. \n" +
            "의뢰서를 확인해주세요."),
    ARCHITECT_PROJECT_ACCEPTANCE("024090000728", "#{의뢰인}님이 #{치자이너}님에게 \n" +
            " #{project_name} 재제작을 요청했습니다."),
    ARCHITECT_PROJECT_CANCELLATION("024090000735", "#{프로젝트}가 취소되었습니다.\n" +
            "\n" +
            "해당 알림 메시지는 \n" +
            "고객님의 알림 신청에 의해 발송됩니다."),
    ARCHITECT_PROJECT_COMPLETION("024090000736", "#{의뢰인}님의  #{project_name}를 지정요청 하였습니다.\n" +
            "의뢰서를 확인해주세요.\n" +
            "\n" +
            "해당 알림 메시지는 \n" +
            "고객님의 알림 신청에 의해 발송됩니다."),
    ARCHITECT_PAYMENT_PROCESSING_INITIATED("024090000738", "#{치과기공소or치자이너}님이 요청하신 마일리지 정산이 완료되었습니다.\n" +
            "\n" +
            "해당 알림 메시지는 \n" +
            "고객님의 알림 신청에 의해 발송됩니다."),
    ARCHITECT_PAYMENT_PROCESSING_COMPLETED("024090000741", "#{치과기공소or치자이너}님이 요청하신 마일리지 정산이 신청되었습니다.\n" +
            "국내 마일리지의 경우 덴트너로 계산서를 발행해주셔야 정산이 완료됩니다.\n" +
            "덴트너 사업자 정보는 마일리지 정산 화면에 있습니다.\n" +
            "\n" +
            "해당 알림 메시지는 \n" +
            "고객님의 알림 신청에 의해 발송됩니다."),

    CANCEL_REQUEST("024090000794", "#{project_name} 요청서의 취소 요청이 접수 되었습니다.\n" +
            "사유를 확인해주세요.\n" +
            "\n" +
            "해당 메시지는 고객님의 알림신청에 의해 발송되었습니다"),

    VIEW_3D_REQUEST("024100000753", "#{의뢰자}님이 작성하신 #{project_name} 에 #{치자이너}님이 3D뷰어 소통을 요청하였습니다.\n" +
            "\n" +
            "'거래내역' -> '3D뷰어 소통' -> 댓글 을 확인해주세요.\n" +
            "\n" +
            "해당 댓글 등록 메시지는 고객님의 알림신청에 의해 발송되었습니다"),

    VIEW_3D_DESIGNER("024100000754", "#{의뢰자}님이 작성하신 #{project_name} '3D뷰어 소통' 에 댓글이 달렸습니다.\n" +
            "\n" +
            "'거래내역' -> '3D뷰어 소통' -> 댓글 을 확인해주세요.\n" +
            "\n" +
            "해당 댓글 등록 메시지는 고객님의 알림신청에 의해 발송되었습니다");

    private final String code;
    private final String messageTemplate;

    AlarmTalkEnum(String code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    public String getCode() {
        return code;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public static AlarmTalkEnum getByCode(String code) {
        for (AlarmTalkEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ProjectStatus code: " + code);
    }

    public String formatMessage(Map<String, String> replacements) {
        String formattedMessage = messageTemplate;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            formattedMessage = formattedMessage.replace("#{" + entry.getKey() + "}", entry.getValue());
        }
        return formattedMessage;
    }

}
