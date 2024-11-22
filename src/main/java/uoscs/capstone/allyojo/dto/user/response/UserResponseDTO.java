package uoscs.capstone.allyojo.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import uoscs.capstone.allyojo.entity.User;


// validation 필요
@Data
@AllArgsConstructor
public class UserResponseDTO {

    private final String username;
    private final String name;
    // usergrade는 디폴트로 BASIC
    private final String phoneNumber;
    private final Integer age;
    private final String gender;

    public static UserResponseDTO fromUser(final User user) {
        return new UserResponseDTO(
                user.getUsername(),
                user.getName(),
                user.getPhoneNumber(),
                user.getAge(),
                user.getGender().name());
    }
}
