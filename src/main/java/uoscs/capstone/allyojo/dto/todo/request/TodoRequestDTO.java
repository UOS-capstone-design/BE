package uoscs.capstone.allyojo.dto.todo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequestDTO {
    private Long todoId;
    private String username; // findUserIdByUsername
    private String taskName;
    private String taskDescription;
    private LocalDate taskDate;
    private LocalDateTime taskTime;
}
