package uoscs.capstone.allyojo.entity;

import jakarta.persistence.Entity;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NutrientId implements Serializable {

    private Long verificationId;
    private String foodName;
}
