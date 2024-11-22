//package uoscs.capstone.allyojo.config;
//
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import uoscs.capstone.allyojo.entity.Guardian;
//import uoscs.capstone.allyojo.entity.Mission;
//import uoscs.capstone.allyojo.entity.User;
//import uoscs.capstone.allyojo.entity.UserGrade;
//import uoscs.capstone.allyojo.repository.GuardianRepository;
//import uoscs.capstone.allyojo.repository.MissionRepository;
//import uoscs.capstone.allyojo.repository.UserRepository;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer {
//    private final MissionRepository missionRepository;
//    private final UserRepository userRepository;
//    private final GuardianRepository guardianRepository;
//
//    @PostConstruct
//    public void init() {
//        createDummyMission();
//    }
//    private void createDummyMission() {
//        Mission mission1 =
//                Mission.builder()
//                        .missionName("Eat Medician")
//                        .description("약")
//                        .build();
//
//        Mission mission2 =
//                Mission.builder()
//                        .missionName("Eat food")
//                        .description("")
//                        .build();
//
//        Mission mission3 =
//                Mission.builder()
//                        .missionName("Manage blood pressure")
//                        .description("혈압")
//                        .build();
//
//        Mission mission4 =
//                Mission.builder()
//                        .missionName("Manage blood sugar")
//                        .description("혈당")
//                        .build();
//
//        User user1 =
//                User.builder()
//                        .username("u1")
//                        .password("12341234")
//                        .name("u1")
//                        .userGrade(UserGrade.BASIC)
//                        .phoneNumber("01012341234")
//                        .build();
//        User user2 =
//                User.builder()
//                        .username("u2")
//                        .password("12341234")
//                        .name("u2")
//                        .userGrade(UserGrade.BASIC)
//                        .phoneNumber("01012341234")
//                        .build();
//
//        Guardian guardian =
//                Guardian.builder()
//                        .guardianName("g")
//                        .password("12341234")
//                        .name("g")
//                        .phoneNumber("01012341234")
//                        .build();
//
//
//        missionRepository.save(mission1);
//        missionRepository.save(mission2);
//        missionRepository.save(mission3);
//        missionRepository.save(mission4);
//        userRepository.save(user1);
//        userRepository.save(user2);
//        guardianRepository.save(guardian);
//    }
//}
