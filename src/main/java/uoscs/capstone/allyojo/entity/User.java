package uoscs.capstone.allyojo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true) // 유니크합니다
    private String username; // 로그인 아이디

    @Column(nullable = false)
    private String password; // 로그인 비밀번호

    @Column(nullable = false)
    private String name; // 사람이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserGrade userGrade; // premium or basic

    @Column(nullable = false)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardianId")
    private Guardian guardian;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Alarm> alarms;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Todo> todoList;

    @Builder
    public User(
            String username,
            String password,
            String name,
            UserGrade userGrade,
            String phoneNumber,
            Guardian guardian) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.userGrade = userGrade;
        this.phoneNumber = phoneNumber;
        this.guardian = guardian;
    }

    public void addGuardian(Guardian guardian) {
        this.guardian = guardian;
    }
}



