package uoscs.capstone.allyojo.dto.verification.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequestDTO {
    private String username;
    private String missionName;
    private LocalDate startDate;
    private LocalDate endDate;


}
