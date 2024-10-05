package uoscs.capstone.allyojo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String guardianPhoneNumber;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Alarm> alarms;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Todo> todoList;
}
