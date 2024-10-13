package uoscs.capstone.allyojo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import uoscs.capstone.allyojo.entity.UserGrade;


// validation 필요
@Data
@AllArgsConstructor
public class UserJoinRequestDTO {

    private final String username;
    private final String password;
    private final String name;
    // usergrade는 디폴트로 BASIC
    private final String phoneNumber;
    private final String guardianPhoneNumber;
}
