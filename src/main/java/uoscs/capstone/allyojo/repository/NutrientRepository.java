package uoscs.capstone.allyojo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uoscs.capstone.allyojo.entity.Nutrient;
import uoscs.capstone.allyojo.entity.NutrientId;

@Repository
public interface NutrientRepository extends JpaRepository<Nutrient, NutrientId> {
}
