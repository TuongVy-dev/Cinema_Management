package vn.edu.fpt.cinemamanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.cinemamanagement.entities.Concession;

import java.util.Optional;

public interface ConcessionRepository extends JpaRepository<Concession, String> {
    Optional<Concession> findTopByConcessionIdStartingWithOrderByConcessionIdDesc(String prefix);

}
