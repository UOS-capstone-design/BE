package uoscs.capstone.allyojo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    @JoinColumn(name = "alarmId")
    private Alarm alarm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    private LocalDateTime verificationDateTime;

    @Column(nullable = true) // nullable. 측정 값이 없는 경우
    private Double value;


    @Column(nullable = true) // nullable, 혈압 미션: 수축기 / 이완기인 경우 사용
    private Double value2;

    private Boolean result;

    @OneToMany(mappedBy = "verification", fetch = FetchType.LAZY)
    private List<Nutrient> nutrients;
}
