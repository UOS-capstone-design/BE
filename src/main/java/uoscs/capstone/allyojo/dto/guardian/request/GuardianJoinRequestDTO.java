package uoscs.capstone.allyojo.dto.guardian.request;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GuardianJoinRequestDTO {

    private final String guardianName;
    private final String password;
    private final String name;
    private final String phoneNumber;

    private final String seniorName; // 노인 아이디 입력, nullable
}
