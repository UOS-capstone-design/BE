package uoscs.capstone.allyojo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uoscs.capstone.allyojo.entity.Nutrient;
import uoscs.capstone.allyojo.entity.NutrientId;
import uoscs.capstone.allyojo.entity.Verification;

import java.util.List;

@Repository
public interface NutrientRepository extends JpaRepository<Nutrient, NutrientId> {
    List<Nutrient> findAllByVerification(Verification verification);
}
