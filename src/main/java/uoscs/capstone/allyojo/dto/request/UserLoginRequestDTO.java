package uoscs.capstone.allyojo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginRequestDTO {
    @NotBlank(message = "아이디를 입력하세요.")
    private final String username;
    @NotBlank(message = "비밀번호를 입력하세요.")
    private final String password;
}
