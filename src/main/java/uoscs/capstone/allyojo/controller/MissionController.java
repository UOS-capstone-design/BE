package uoscs.capstone.allyojo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uoscs.capstone.allyojo.dto.mission.request.MissionDTO;
import uoscs.capstone.allyojo.entity.Mission;
import uoscs.capstone.allyojo.service.MissionService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mission")
@Tag(name = "미션", description = "미션 항목 추가 api입니다. 관리자용이며, 사용자에게 제공되지 않습니다.")
public class MissionController {

    private final MissionService missionService;

    // 미션 등록
    @PostMapping("/add")
    @Operation(summary = "미션 추가", description = "새 미션을 추가합니다.")
    public ResponseEntity<Mission> addMission(@RequestBody MissionDTO missionDTO) {
        Mission savedMission = missionService.addMission(missionDTO);
        return ResponseEntity.ok(savedMission);
    }

    // 모든 미션 조회
    @GetMapping("/findAll")
    @Operation(summary = "모든 미션 검색", description = "모든 미션을 조회합니다.")
    public ResponseEntity<List<Mission>> findAll() {
        List<Mission> missionList = missionService.getAllMissions();
        return ResponseEntity.ok(missionList);
    }

    // 미션 수정
    @PutMapping("/update")
    @Operation(summary = "미션 수정", description = "미션을 수정합니다.")
    public ResponseEntity<Mission> updateMission(@RequestBody MissionDTO missionDTO) {
        Mission updatedMission = missionService.updateMission(missionDTO);
        return ResponseEntity.ok(updatedMission);
    }

    // 미션 삭제
    @DeleteMapping("delete/{missionId}")
    @Operation(summary = "미션 삭제", description = "미션을 삭제합니다.")
    public ResponseEntity<String> deleteMission(@PathVariable Long missionId) {
        missionService.deleteMission(missionId);
        return ResponseEntity.ok("미션이 삭제되었습니다.");
    }


}
