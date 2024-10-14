package uoscs.capstone.allyojo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import uoscs.capstone.allyojo.entity.User;

@Data
@AllArgsConstructor
public class UserJoinResponseDTO {
    private final String username;
    private final String name;
    private final String phoneNumber;
    private final String guardianPhoneNumber;

    public static UserJoinResponseDTO fromUser(final User user) {
        return new UserJoinResponseDTO(
                user.getUsername(),
                user.getName(),
                user.getPhoneNumber(),
                user.getGuardianPhoneNumber());
    }
}
