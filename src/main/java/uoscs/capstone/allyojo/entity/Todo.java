package uoscs.capstone.allyojo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Todo {

    // 유저아이디, 할일이름, 할일설명, 날짜시간
    @Id
    @Column(unique = true, nullable = false)
    // @GeneratedValue(strategy = GenerationType.IDENTITY) 아이디 만들어서 주는거로 가정
    private Long todoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false)
    private String taskName;

    @Column(nullable = false)
    private String taskDescription;

    @Column(nullable = false)
    private LocalDateTime taskDateTime;



}
