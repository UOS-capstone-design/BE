package uoscs.capstone.allyojo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uoscs.capstone.allyojo.entity.Mission;
import uoscs.capstone.allyojo.exception.mission.MissionNotFoundException;
import uoscs.capstone.allyojo.repository.MissionRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MissionService {
    private final MissionRepository missionRepository;

    public Mission addMission(Mission mission) {
        return missionRepository.save(mission);
    }

    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    public Mission updateMission(Mission mission) {
        return missionRepository.save(mission);
    }

    public void deleteMission(Long MissionId) {
        Mission mission = missionRepository.findById(MissionId)
                .orElseThrow(MissionNotFoundException::new);
        missionRepository.delete(mission);
    }
}
