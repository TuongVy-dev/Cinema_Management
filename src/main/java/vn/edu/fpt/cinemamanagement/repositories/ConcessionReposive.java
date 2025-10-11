package vn.edu.fpt.cinemamanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.cinemamanagement.entities.ConcessionEntity;

import java.util.Optional;

public interface ConcessionReposive extends JpaRepository<ConcessionEntity, String> {
    Optional<ConcessionEntity> findTopByConcessionIdStartingWithOrderByConcessionIdDesc(String prefix);
}
