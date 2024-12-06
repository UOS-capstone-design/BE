//package uoscs.capstone.allyojo.auth.coolsms;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.nurigo.sdk.message.model.Message;
//import net.nurigo.sdk.message.response.SingleMessageSentResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import uoscs.capstone.allyojo.exception.coolsms.CoolsmsException;
//
//import java.util.HashMap;
//import java.util.Random;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CoolSmsService {
//    @Value("${coolsms.api.key}")
//    private String apiKey;
//    @Value("${coolsms.api.secret}")
//    private String apiSecret;
//    @Value("${coolsms.api.number}")
//    private String allyojoPhoneNumber;
//
//    public String sendVerificationNumber(String phoneNumber) throws CoolsmsException {
//        try {
//            // 랜덤한 4자리 인증번호 생성
//            String verificationNumber = generateRandomNumber();
//
//            Message message = new Message();
//            message.setFrom(allyojoPhoneNumber);
//            message.setTo(phoneNumber);
//            message.setText("[Allyojo] 인증번호는 [" + verificationNumber + "] 입니다.");
//
//            SingleMessageSentResponse response = this.messageService.sendOne
//
//
//
//
//            // 메시지 전송
//            message.send(params);
//
//            return verificationNumber; // 생성된 인증번호 반환
//        } catch (Exception e) {
//            throw new CoolsmsException("Failed to send SMS", e);
//        }
//    }
//
//    private String generateRandomNumber() {
//        Random rand = new Random();
//        StringBuilder numStr = new StringBuilder();
//        for (int i = 0; i < 4; i++) {
//            numStr.append(rand.nextInt(10));
//        }
//        return numStr.toString();
//    }
//}
