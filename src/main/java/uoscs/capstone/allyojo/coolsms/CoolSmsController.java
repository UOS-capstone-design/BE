package uoscs.capstone.allyojo.coolsms;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/coolsms")
@Tag(name = "전화번호 인증")
public class CoolSmsController {

    private final CoolSmsService coolSmsService;

    @PostMapping
    public SingleMessageSentResponse verificationPhoneNumber(PhoneNumberVerificationDTO dto) {

        SingleMessageSentResponse response = this.coolSmsService.sendVerificationCode(dto.getPhoneNumber());
        log.info("coolsms response = {}", response);
        return response;
    }
}
