package uoscs.capstone.allyojo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Guardian {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guardianId;

    @Column(nullable = false)
    private String guardianName; // 로그인 아이디

    @Column(nullable = false)
    private String password; // 로그인 비밀번호

    @Column(nullable = false)
    private String name; // 사람이름

    @Column(nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "guardian", fetch = FetchType.LAZY)
    private List<User> users;

    public void addManagedUser(User user) {
        users.add(user);
    }
}
