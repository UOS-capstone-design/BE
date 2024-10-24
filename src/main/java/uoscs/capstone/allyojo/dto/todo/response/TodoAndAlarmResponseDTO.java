package uoscs.capstone.allyojo.dto.todo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uoscs.capstone.allyojo.dto.alarm.response.AlarmResponseDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoAndAlarmResponseDTO {
    private List<TodoResponseDTO> todos;
    private List<AlarmResponseDTO> alarms;
}
