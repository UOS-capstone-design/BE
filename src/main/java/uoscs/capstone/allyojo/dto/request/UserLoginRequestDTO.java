package uoscs.capstone.allyojo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginRequestDTO {
    private final String username;
    private final String password;
}
