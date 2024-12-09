package uoscs.capstone.allyojo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uoscs.capstone.allyojo.entity.Guardian;

import java.util.Optional;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {

    Optional<Guardian> findByGuardianName(String guardianName);

    boolean existsByGuardianName(String guardianName);

    boolean existsByPhoneNumber(String guardianPhone);
}
