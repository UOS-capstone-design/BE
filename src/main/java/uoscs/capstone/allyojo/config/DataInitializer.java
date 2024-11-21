//package uoscs.capstone.allyojo.config;
//
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import uoscs.capstone.allyojo.entity.Mission;
//import uoscs.capstone.allyojo.repository.MissionRepository;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer {
//    private final MissionRepository missionRepository;
//
//    @PostConstruct
//    public void init() {
//        createDummyMission();
//    }
//    private void createDummyMission() {
//        Mission mission1 =
//                Mission.builder()
//                        .missionName("약복용")
//                        .description("약")
//                        .build();
//
//        Mission mission2 =
//                Mission.builder()
//                        .missionName("식사관리")
//                        .description("밥")
//                        .build();
//
//        Mission mission3 =
//                Mission.builder()
//                        .missionName("혈압관리")
//                        .description("혈압")
//                        .build();
//
//        Mission mission4 =
//                Mission.builder()
//                        .missionName("혈당관리")
//                        .description("혈당")
//                        .build();
//
//        missionRepository.save(mission1);
//        missionRepository.save(mission2);
//        missionRepository.save(mission3);
//        missionRepository.save(mission4);
//    }
//}
