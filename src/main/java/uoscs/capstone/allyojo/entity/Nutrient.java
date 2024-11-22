package uoscs.capstone.allyojo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Nutrient {

    @EmbeddedId
    private NutrientId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("verificationId")
    @JoinColumn(name = "verificationId", nullable = false)
    private Verification verification;
//
//    @Id
//    @Column(nullable = false)
//    private String foodName;

    @Column(nullable = false)
    private Double carbohydrates;

    @Column(nullable = false)
    private Double protein;

    @Column(nullable = false)
    private Double fat;

    @Column(nullable = false)
    private Double sodium;

    public static class NutrientBuilder {
        private Long verificationId;
        private String foodName;

        public NutrientBuilder verificationId(Long verificationId) {
            this.verificationId = verificationId;
            return this;
        }

        public NutrientBuilder foodName(String foodName) {
            this.foodName = foodName;
            return this;
        }

        public Nutrient build() {
            this.id = new NutrientId(verificationId, foodName);
            return new Nutrient(id, verification, carbohydrates, protein, fat, sodium);
        }
    }
}
