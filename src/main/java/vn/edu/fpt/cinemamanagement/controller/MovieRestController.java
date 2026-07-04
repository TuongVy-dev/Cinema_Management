package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.cinemamanagement.dto.MovieDTO;
import vn.edu.fpt.cinemamanagement.entities.Movie;
import vn.edu.fpt.cinemamanagement.repositories.MovieRepository;
import vn.edu.fpt.cinemamanagement.services.MovieService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MovieRestController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping
    public ResponseEntity<Page<MovieDTO>> listMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> movies;

        if (search != null && !search.trim().isEmpty()) {
            movies = movieRepository.searchByTitle(search.trim(), pageable);
        } else {
            movies = movieService.getAllMovies(pageable);
        }

        Page<MovieDTO> dtoPage = movies.map(MovieDTO::fromEntity);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovie(@PathVariable String id) {
        Movie movie = movieService.findById(id);
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(MovieDTO.fromEntity(movie));
    }

    @GetMapping("/next-id")
    public ResponseEntity<Map<String, String>> getNextId() {
        String nextId = movieService.generateMovieID();
        Map<String, String> response = new HashMap<>();
        response.put("nextId", nextId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMovie(
            @RequestParam String title,
            @RequestParam String genre,
            @RequestParam(required = false) String summary,
            @RequestParam int duration,
            @RequestParam String releaseDate,
            @RequestParam String ageRating,
            @RequestParam(required = false) String trailer,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        Movie movie = new Movie();
        movie.setMovieID(movieService.generateMovieID());
        movie.setTitle(title != null ? title.trim() : "");
        movie.setGenre(genre);
        movie.setSummary(summary != null ? summary.trim() : "");
        movie.setDuration(duration);
        movie.setReleaseDate(LocalDate.parse(releaseDate));
        movie.setAgeRating(ageRating);
        movie.setTrailer(trailer != null ? trailer.trim() : "");

        if (image != null && !image.isEmpty()) {
            String imagePath = saveImage(image);
            movie.setImg(imagePath);
        } else {
            movie.setImg("");
        }

        Map<String, String> errors = movieService.createMovie(movie);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(MovieDTO.fromEntity(movie));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMovie(
            @PathVariable String id,
            @RequestParam String title,
            @RequestParam String genre,
            @RequestParam(required = false) String summary,
            @RequestParam int duration,
            @RequestParam String releaseDate,
            @RequestParam String ageRating,
            @RequestParam(required = false) String trailer,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        Movie existing = movieService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setTitle(title != null ? title.trim() : "");
        existing.setGenre(genre);
        existing.setSummary(summary != null ? summary.trim() : "");
        existing.setDuration(duration);
        existing.setReleaseDate(LocalDate.parse(releaseDate));
        existing.setAgeRating(ageRating);
        existing.setTrailer(trailer != null ? trailer.trim() : "");

        if (image != null && !image.isEmpty()) {
            String imagePath = saveImage(image);
            existing.setImg(imagePath);
        }

        Map<String, String> errors = movieService.updateMovie(existing);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok(MovieDTO.fromEntity(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String id) {
        Movie movie = movieService.findById(id);
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }
        movieService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private String saveImage(MultipartFile file) {
        try {
            File uploadDir = new ClassPathResource("static/assets/img/movies").getFile();
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetPath = uploadDir.toPath().resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/assets/img/movies/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }
}
