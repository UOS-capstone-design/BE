package uoscs.capstone.allyojo.coolsms;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Getter
public class CoolSmsService {
    @Value("${coolsms.apiKey}")
    private String apiKey;
    @Value("${coolsms.secret}")
    private String secret;
    @Value("${coolsms.number}")
    private String number;
    @Value("${coolsms.domain}")
    private String domain;

    DefaultMessageService messageService;
    @PostConstruct // 의존성 주입이 완료된 후 초기화를 수행하는 메서드
    public void init(){
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, secret, domain);
    }

    public SingleMessageSentResponse sendVerificationCode(String phoneNumber) {
        Message message = new Message();
        message.setFrom(number);
        message.setTo(phoneNumber);
        message.setText("[Allyojo] 인증번호는 [" + generateVerificationCode() + "] 입니다.");
        return this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    public String generateVerificationCode() {
        Random rand = new Random();
        StringBuilder verificationCode = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            verificationCode.append(rand.nextInt(10));
        }
        return verificationCode.toString();
    }
}
