package uoscs.capstone.allyojo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uoscs.capstone.allyojo.entity.Alarm;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
