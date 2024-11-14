package uoscs.capstone.allyojo.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uoscs.capstone.allyojo.dto.alarm.response.AlarmResponseDTO;
import uoscs.capstone.allyojo.dto.todo.request.TodoRequestDTO;
import uoscs.capstone.allyojo.dto.todo.response.TodoAndAlarmResponseDTO;
import uoscs.capstone.allyojo.dto.todo.response.TodoResponseDTO;
import uoscs.capstone.allyojo.entity.Todo;
import uoscs.capstone.allyojo.service.AlarmService;
import uoscs.capstone.allyojo.service.TodoService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/todo")
@Tag(name = "알려줘 탭")
public class TodoController {
    private final TodoService todoService;
    private final AlarmService alarmService;

    @PostMapping("/add")
    @Operation(summary = "투두 추가", description = "투두리스트 항목을 추가합니다.")
    public ResponseEntity<TodoResponseDTO> addTodo(@Valid @RequestBody TodoRequestDTO todoRequestDTO) {
        Todo todo = todoService.addTodo(todoRequestDTO);
        TodoResponseDTO todoResponseDTO = TodoResponseDTO.fromTodo(todo);
        return ResponseEntity.ok(todoResponseDTO);
    }

    @GetMapping("/{username}/only_todo")
    @Operation(summary = "투두 조회 (투두리스트만)", description = "해당 username의 투두리스트 목록을 모두 가져옵니다. 알람은 제외됩니다. ")
    public ResponseEntity<List<TodoResponseDTO>> getTodosByUsername(@PathVariable("username") String username) {
        List<TodoResponseDTO> todoResponseDTOs = todoService
                .findAllTodoByUsername(username)
                .stream()
                .map(TodoResponseDTO::fromTodo)
                .toList();
        return ResponseEntity.ok(todoResponseDTOs);
    }

    @GetMapping("/{username}")
    @Operation(summary = "투두 조회 (알람 포함)", description = "해당 username의 투두리스트와 미션이 수행되지 않은 알람 목록을 가져옵니다. ")
    public ResponseEntity<TodoAndAlarmResponseDTO> getTodosAndConditionedAlarmsByUsername(@PathVariable("username") String username) {
        List<TodoResponseDTO> todoResponseDTOs = todoService
                .findAllTodoByUsername(username)
                .stream()
                .map(TodoResponseDTO::fromTodo)
                .toList();

        List<AlarmResponseDTO> alarmResponseDTOs = alarmService
                .findAlarmsByUsernameForTodo(username)
                .stream()
                .map(AlarmResponseDTO::fromAlarm)
                .toList();

        TodoAndAlarmResponseDTO responseDTO = TodoAndAlarmResponseDTO.builder()
                .todos(todoResponseDTOs)
                .alarms(alarmResponseDTOs)
                .build();

        return ResponseEntity.ok(responseDTO);

    }

    @PutMapping("/update")
    @Operation(summary = "투두 수정", description = "투두리스트 항목을 수정합니다.")
    public ResponseEntity<TodoResponseDTO> updateTodo(@Valid @RequestBody TodoRequestDTO todoRequestDTO) {
        Todo updatedTodo = todoService.updateTodo(todoRequestDTO);
        TodoResponseDTO todoResponseDTO = TodoResponseDTO.fromTodo(updatedTodo);
        return ResponseEntity.ok(todoResponseDTO);
    }

    @DeleteMapping("delete/{todoId}")
    @Operation(summary = "투두 삭제", description = "투두리트스 항목을 삭제합니다.")
    public ResponseEntity<String> deleteTodo(@PathVariable Long todoId) {
        todoService.deleteTodo(todoId);
        return ResponseEntity.ok("투두 항목이 삭제되었습니다.");
    }
}
