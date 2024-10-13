package uoscs.capstone.allyojo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uoscs.capstone.allyojo.entity.Mission;


@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
}
