package uoscs.capstone.allyojo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Verification {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long verificationId;

    // 알람아이디, 검증일시, 검증결과, 측정값
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id")
    private Alarm alarm;

    @Column(nullable = false)
    private LocalDateTime verificationDateTime;

    @Column(nullable = false)
    private String result;

    @Column(nullable = true) // nullable. 측정 값이 없는 경우
    private Double value;
}
