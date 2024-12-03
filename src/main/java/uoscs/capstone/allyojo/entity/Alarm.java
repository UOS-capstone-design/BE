package uoscs.capstone.allyojo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Alarm {

    @Id
    @Column(unique = true, nullable = false)
    private Long alarmId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missionId", nullable = false)
    private Mission mission;

    @Column(nullable = false) // 알람시간: LocalTime
    private LocalDateTime alarmTime;

    @Column(nullable = true) // title: 어떤 알람인지
    private String title;

    @Column(nullable = false) // 알람활성여부
    private Boolean active;

    @Column(nullable = false) // 알람요일: 7자리 비트
    private Integer alarmDays = 0;

    @Column(nullable = false) // 지연횟수: 몇 번 울렸는지
    private Integer delayTimes;

    @Column(nullable = false)  // 엄격모드: Boolean
    private Boolean restrictAlarm;

    @Column(nullable = false)  // 진동여부: Boolean
    private Boolean isVibration;

    @Column(nullable = false) // 음향크기
    private Integer volume;

    @Column(nullable = false) // 반복간격: Integer (분 단위 저장)
    private Integer alarmInterval;

    @OneToMany(mappedBy = "alarm", fetch = FetchType.LAZY)
    private List<Verification> verifications;

    // 보호자로부터 생성
    @Column(nullable = false)
    private Boolean createdByGuardian = false;

    // 알람 삭제 시 -> disabled = true
    @Column(nullable = false)
    private Boolean disabled = false;

    //// 알람요일 관련 메서드
    // 요일별 알람 비트를 체크하는 메서드
    public boolean isAlarmSetForDay(int dayIndex) {
        // dayIndex: 0 (월요일) ~ 6 (일요일)
        return (alarmDays & (1 << dayIndex)) != 0;
    }

    // 특정 요일에 알람을 설정하는 메서드
    public void setAlarmForDay(int dayIndex) {
        // dayIndex: 0 (월요일) ~ 6 (일요일)
        alarmDays |= (1 << dayIndex);
    }

    // 특정 요일의 알람을 해제하는 메서드
    public void unsetAlarmForDay(int dayIndex) {
        alarmDays &= ~(1 << dayIndex);
    }

    // 전체 알람 요일을 출력하는 메서드 (디버깅용)
    public String getAlarmDaysAsBinary() {
        return String.format("%07d", Integer.parseInt(Integer.toBinaryString(alarmDays)));
    }

    public void update(
            Mission mission,
            LocalDateTime alarmTime,
            String title,
            Boolean active,
            Integer alarmDays,
            Integer delayTimes,
            Boolean restrictAlarm,
            Boolean isVibration,
            Integer volume,
            Integer alarmInterval,
            Boolean createdByGuardian,
            Boolean disabled
    ) {
        this.mission = mission;
        this.alarmTime = alarmTime;
        this.title = title;
        this.active = active;
        this.alarmDays = alarmDays;
        this.delayTimes = delayTimes;
        this.restrictAlarm = restrictAlarm;
        this.isVibration = isVibration;
        this.volume = volume;
        this.alarmInterval = alarmInterval;
        this.createdByGuardian = createdByGuardian;
        this.disabled = disabled;
    }
}
