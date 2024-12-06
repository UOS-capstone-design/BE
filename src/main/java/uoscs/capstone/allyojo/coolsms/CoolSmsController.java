package uoscs.capstone.allyojo.coolsms;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/coolsms")
@Tag(name = "전화번호 인증")
public class CoolSmsController {

    private final CoolSmsService coolSmsService;

    @PostMapping
    @Operation(summary = "인증번호 발송", description = "coolsms api를 활용하여 인증번호를 생성하고 발송합니다.")
    public ResponseEntity<PhoneNumberVerificationResponseDTO> verificationPhoneNumber(PhoneNumberVerificationRequsetDTO dto) {
        String verificationCode = this.coolSmsService.sendVerificationCode(dto.getPhoneNumber());
        return ResponseEntity.ok(new PhoneNumberVerificationResponseDTO(dto.getPhoneNumber(), verificationCode));
    }
}
