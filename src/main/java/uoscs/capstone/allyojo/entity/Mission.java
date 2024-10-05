package uoscs.capstone.allyojo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Mission {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long missionId;

    @Column(nullable = false)
    private String missionName;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "mission", fetch = FetchType.LAZY)
    private List<Alarm> alarms;

}
