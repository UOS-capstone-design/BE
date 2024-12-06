package uoscs.capstone.allyojo.coolsms;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhoneNumberVerificationRequsetDTO {
    private final String phoneNumber;
}
