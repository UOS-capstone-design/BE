package uoscs.capstone.allyojo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uoscs.capstone.allyojo.entity.Verification;


@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {
}
