package uoscs.capstone.allyojo.dto.guardian.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import uoscs.capstone.allyojo.entity.Guardian;

@Data
@AllArgsConstructor
public class GuardianResponseDTO {

    private final String guardianName;
    private final String name;
    private final String phoneNumber;

    public static GuardianResponseDTO fromGuardian(final Guardian guardian) {
        return new GuardianResponseDTO(
                guardian.getGuardianName(),
                guardian.getName(),
                guardian.getPhoneNumber()
        );
    }
}
