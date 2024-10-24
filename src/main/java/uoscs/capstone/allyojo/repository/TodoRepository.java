package uoscs.capstone.allyojo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uoscs.capstone.allyojo.entity.Todo;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUserUserId(Long userId);
}
