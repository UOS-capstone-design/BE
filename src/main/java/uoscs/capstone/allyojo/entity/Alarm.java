package uoscs.capstone.allyojo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Alarm {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    // 요일, 시간, 엄격, 반복간격, 진동

    // 알람요일: 7자리 비트
    @Column(nullable = false)
    private Integer alarmDays = 0;

    // 알람시간: LocalTime
    @Column(nullable = false)
    private LocalTime alarmTime;

    // 엄격모드: Boolean
    @Column(nullable = false)
    private Boolean strictMode;

    // 반복간격: Integer (분 단위 저장)
    @Column(nullable = false)
    private Integer repeatInterval;

    // 진동여부: Boolean
    @Column(nullable = false)
    private Boolean useVibration;

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

}
