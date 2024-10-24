package uoscs.capstone.allyojo.dto.todo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uoscs.capstone.allyojo.entity.Todo;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoResponseDTO {
    private Long todoId;
    private String taskName;
    private String taskDescription;
    private LocalDateTime taskDateTime;

    // 엔티티로부터 ResponseDTO 생성
    public static TodoResponseDTO fromTodo(Todo todo) {
        return TodoResponseDTO.builder()
                .todoId(todo.getTodoId())
                .taskName(todo.getTaskName())
                .taskDescription(todo.getTaskDescription())
                .taskDateTime(todo.getTaskDateTime())
                .build();
    }
}
