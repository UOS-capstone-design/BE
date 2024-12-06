package uoscs.capstone.allyojo.coolsms;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhoneNumberVerificationResponseDTO {
    private final String phoneNumber;
    private final String verificationCode;
}
