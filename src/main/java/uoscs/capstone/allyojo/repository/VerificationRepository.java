package uoscs.capstone.allyojo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uoscs.capstone.allyojo.entity.Verification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {

    // 날짜 사이의 verificationDateTime를 만족하는 verification을 모아 리턴
    @Query("select v from Verification v where v.verificationDateTime between :startDate and :endDate")
    List<Verification> findAllBetweenDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<Verification> findByUserUsernameAndAlarmMissionMissionIdAndVerificationDateTimeBetween(
            String username,
            Long missionId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("select distinct v.alarm.mission.missionName from Verification v where v.user.username = :username")
    List<String> findDistinctMissionNamesByUsername(String username);
}
