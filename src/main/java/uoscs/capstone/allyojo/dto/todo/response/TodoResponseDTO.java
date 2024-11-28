package uoscs.capstone.allyojo.dto.todo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uoscs.capstone.allyojo.entity.Todo;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoResponseDTO {
    private Long todoId;
    private String taskName;
    private String taskDescription;
    private LocalDate taskDate;
    private LocalDateTime taskTime;

    // 엔티티로부터 ResponseDTO 생성
    public static TodoResponseDTO fromTodo(Todo todo) {
        return TodoResponseDTO.builder()
                .todoId(todo.getTodoId())
                .taskName(todo.getTaskName())
                .taskDescription(todo.getTaskDescription())
                .taskDate(todo.getTaskDate())
                .taskTime(todo.getTaskTime())
                .build();
    }
}
