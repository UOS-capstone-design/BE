package uoscs.capstone.allyojo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Nutrient {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verificationId", nullable = false)
    private Verification verification;

    @Id
    @Column(nullable = false)
    private String foodName;

    @Column(nullable = false)
    private Double carbohydrates;

    @Column(nullable = false)
    private Double protein;

    @Column(nullable = false)
    private Double fat;

    @Column(nullable = false)
    private Double sodium;
}
