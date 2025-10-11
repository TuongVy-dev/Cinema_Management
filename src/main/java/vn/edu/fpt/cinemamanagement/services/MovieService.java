package vn.edu.fpt.cinemamanagement.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.fpt.cinemamanagement.entities.Movie;
import vn.edu.fpt.cinemamanagement.repositories.MovieRepository;

import java.util.List;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;
    @Transactional
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }
    @Transactional
    public Movie findById(String id) {
        return movieRepository.findByMovieID(id);
    }
    @Transactional
    public void save(Movie movie) {
        movieRepository.save(movie);
    }
    @Transactional
    public void delete(Movie movie) {
        movieRepository.delete(movie);
    }

}
