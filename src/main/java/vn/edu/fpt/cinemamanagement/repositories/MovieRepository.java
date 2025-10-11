package vn.edu.fpt.cinemamanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.cinemamanagement.entities.Movie;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    Movie findByMovieID(String movieID);

    // Huynh Anh - Added to filter currently showing movies
    List<Movie> findByReleaseDateLessThanEqual(LocalDate date);
}
