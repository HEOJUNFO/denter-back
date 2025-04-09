package com.dentner.core.util;

import com.dentner.core.cmmn.dto.AlarmTalkDto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

import static org.apache.ibatis.io.Resources.getResourceAsStream;

public class EmailUtil {

    public static String encryptEmail(String email) {
        try {
            SecretKeySpec key = new SecretKeySpec(EncryptionUtil.secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(email.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptEmail(String encryptedEmail) {
        try {
            SecretKeySpec key = new SecretKeySpec(EncryptionUtil.secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedEmail);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String readHTMLTemplate(String htmlTemplate) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getResourceAsStream(htmlTemplate);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static String readReplyHTMLTemplate(AlarmTalkDto alarmTalkDto) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getResourceAsStream("reply.html");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("#{요청서 명}", alarmTalkDto.getRequestFormSj());
                line = line.replace("#{의뢰인}", alarmTalkDto.getRequestNickName());
                line = line.replace("#{댓글 내용}", alarmTalkDto.getMsg());
                line = line.replace("#{no}", alarmTalkDto.getRequestFormNo());
                contentBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static String readReplyReHTMLTemplate(AlarmTalkDto alarmTalkDto) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getResourceAsStream("reply_re.html");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("#{요청서 명}", alarmTalkDto.getRequestFormSj());
                line = line.replace("#{의뢰인}", alarmTalkDto.getRequestNickName());
                line = line.replace("#{댓글 내용}", alarmTalkDto.getMsg());
                line = line.replace("#{no}", alarmTalkDto.getRequestFormNo());
                contentBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static String readCadUploadHTMLTemplate(AlarmTalkDto alarmTalkDto) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getResourceAsStream("cad_upload.html");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("#{요청서 명}", alarmTalkDto.getRequestFormSj());
                line = line.replace("#{의뢰인}", alarmTalkDto.getRequestNickName());
                line = line.replace("#{치자이너}", alarmTalkDto.getDesignerNickName());
                contentBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static String readJoinApprovedHTMLTemplate(AlarmTalkDto alarmTalkDto) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getResourceAsStream("join_approved.html");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static String readJoinDeniedHTMLTemplate(AlarmTalkDto alarmTalkDto) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getResourceAsStream("join_denied.html");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("#{회원명}", alarmTalkDto.getMemberNickName());
                contentBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static String readReceiveHTMLTemplate(AlarmTalkDto alarmTalkDto) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getResourceAsStream("received.html");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("#{요청서명}", alarmTalkDto.getRequestFormSj());
                line = line.replace("#{의뢰인}", alarmTalkDto.getRequestNickName());
                line = line.replace("#{치자이너}", alarmTalkDto.getDesignerNickName());
                contentBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static String read3dViewerHTMLTemplate(AlarmTalkDto alarmTalkDto) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getResourceAsStream("viewer_3d.html");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("#{요청서명}", alarmTalkDto.getRequestFormSj());
                line = line.replace("#{의뢰인}", alarmTalkDto.getRequestNickName());
                line = line.replace("#{치자이너}", alarmTalkDto.getDesignerNickName());
                line = line.replace("#{no}", alarmTalkDto.getRequestFormNo());
                contentBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static String readDeadlineHTMLTemplate(AlarmTalkDto alarmTalkDto) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getResourceAsStream("deadline.html");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("#{요청서명}", alarmTalkDto.getRequestFormSj());
                line = line.replace("#{의뢰인}", alarmTalkDto.getRequestNickName());
                contentBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static String readEstimateHTMLTemplate(AlarmTalkDto alarmTalkDto) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getResourceAsStream("estimate.html");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("#{요청서명}", alarmTalkDto.getRequestFormSj());
                line = line.replace("#{의뢰인}", alarmTalkDto.getRequestNickName());
                line = line.replace("#{치자이너}", alarmTalkDto.getRequestNickName());
                line = line.replace("#{no}", alarmTalkDto.getDesignerNo());
                contentBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static String readCancelHTMLTemplate(AlarmTalkDto alarmTalkDto) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = getResourceAsStream("cancel.html");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("#{요청서명}", alarmTalkDto.getRequestFormSj());
                line = line.replace("#{의뢰인}", alarmTalkDto.getRequestNickName());
                contentBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
