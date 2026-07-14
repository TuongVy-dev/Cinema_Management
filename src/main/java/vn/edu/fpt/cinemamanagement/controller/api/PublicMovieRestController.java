package vn.edu.fpt.cinemamanagement.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.cinemamanagement.entities.Movie;
import vn.edu.fpt.cinemamanagement.services.MovieService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/movies")
public class PublicMovieRestController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/top")
    public ResponseEntity<?> getTopMovies() {
        List<Movie> top5Movies = movieService.getTop5Movies();
        
        List<Map<String, Object>> result = top5Movies.stream().map(movie -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("movieID", movie.getMovieID());
            map.put("title", movie.getTitle());
            map.put("genre", movie.getGenre());
            map.put("duration", movie.getDuration());
            map.put("img", movie.getImg() != null ? movie.getImg() : "");
            map.put("ageRating", movie.getAgeRating() != null ? movie.getAgeRating() : "");
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
