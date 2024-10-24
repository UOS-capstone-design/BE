package uoscs.capstone.allyojo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.dto.todo.request.TodoRequestDTO;
import uoscs.capstone.allyojo.entity.Todo;
import uoscs.capstone.allyojo.entity.User;
import uoscs.capstone.allyojo.exception.todo.TodoNotFoundException;
import uoscs.capstone.allyojo.exception.user.UserNotFoundException;
import uoscs.capstone.allyojo.repository.TodoRepository;
import uoscs.capstone.allyojo.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public Todo addTodo(TodoRequestDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(UserNotFoundException::new);

        Todo todo = Todo.builder()
                .todoId(dto.getTodoId())
                .user(user)
                .taskName(dto.getTaskName())
                .taskDescription(dto.getTaskDescription())
                .taskDateTime(dto.getTaskDateTime())
                .build();

        return todoRepository.save(todo);
    }

    public List<Todo> findAllTodoByUserId(Long userId) {
        return todoRepository.findAllByUserUserId(userId);
    }

    public Todo updateTodo(TodoRequestDTO dto) {
        Todo todo = todoRepository.findById(dto.getTodoId())
                .orElseThrow(TodoNotFoundException::new);

        todo = Todo.builder()
                .todoId(todo.getTodoId())
                .user(todo.getUser())
                .taskName(todo.getTaskName())
                .taskDescription(todo.getTaskDescription())
                .taskDateTime(todo.getTaskDateTime())
                .build();

        return todoRepository.save(todo);
    }

    public void deleteTodo(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(TodoNotFoundException::new);
        todoRepository.delete(todo);
    }
}
