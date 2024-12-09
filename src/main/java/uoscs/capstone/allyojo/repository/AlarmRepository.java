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

    List<Alarm> findAllByUserUsername(String username);

    // 활성화된 알람만 가져옴
    List<Alarm> findAllByUserUsernameAndDisabledIsFalse(String username);

    List<Alarm> findAllByUserUsernameAndMissionMissionName(String username, String missionName);

    @Query("select a from Alarm a where a.user.username = :username and a.alarmDays <> 0 and a.delayTimes >= 1")
    List<Alarm> findAlarmsByUsernameForTodo(@Param("username") String username);

    @Query("select a from Alarm a where a.user.username = :username and a.createdByGuardian = true")
    List<Alarm> findAllByUserUsernameAndCreatedByGuardian(@Param("username") String username);

    @Query("select a from Alarm a where a.user.guardian.guardianName = :guardianName and a.createdByGuardian = true and a.disabled = false")
    List<Alarm> findAllByGuardianName(@Param("guardianName") String guardianName);

}
