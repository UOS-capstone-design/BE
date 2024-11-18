package uoscs.capstone.allyojo.dto.guardian.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddUserToGuardianRequestDTO {
    private final String guardianName;
    private final String userPhoneNumber;
}
