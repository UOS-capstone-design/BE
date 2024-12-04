package uoscs.capstone.allyojo.dto.guardian.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uoscs.capstone.allyojo.dto.user.response.UserResponseDTO;
import uoscs.capstone.allyojo.entity.User;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class FindAllUsersResponseDTO {
    private final String username;
    private final String name;
    private final String phoneNumber;
    private final Integer age;
    private final String gender;
    private final List<String> missions;
}
