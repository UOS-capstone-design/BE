package uoscs.capstone.allyojo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uoscs.capstone.allyojo.entity.Alarm;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByUserUserId(Long userId);

    @Query("select a from Alarm a where a.user.userId = :userId and a.alarmDays <> 0 and a.delayTimes >= 1")
    List<Alarm> findAlarmsByUserIdForTodo(@Param("userId") Long userId);
}
