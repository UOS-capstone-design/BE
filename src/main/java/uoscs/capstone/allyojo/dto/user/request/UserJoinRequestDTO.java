package uoscs.capstone.allyojo.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import uoscs.capstone.allyojo.entity.UserGrade;


// validation 필요
@Data
@AllArgsConstructor
public class UserJoinRequestDTO {

    @NotBlank(message = "아이디를 입력하세요.")
    private final String username;
    @NotBlank(message = "비밀번호를 입력하세요.")
    private final String password;
    @NotBlank(message = "성함을 입력하세요.")
    private final String name;
    // usergrade는 디폴트로 BASIC
    @NotBlank(message = "전화번호를 입력하세요.")
    @Schema(example = "01012341234")
    private final String phoneNumber;
}
