package vn.edu.fpt.cinemamanagement.controller.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.cinemamanagement.dto.request.MovieRequestDTO;
import vn.edu.fpt.cinemamanagement.dto.response.MovieResponseDTO;
import vn.edu.fpt.cinemamanagement.entities.Movie;
import vn.edu.fpt.cinemamanagement.repositories.MovieRepository;
import vn.edu.fpt.cinemamanagement.services.MovieService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
    public ResponseEntity<Page<MovieResponseDTO>> listMovies(
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

        Page<MovieResponseDTO> dtoPage = movies.map(MovieResponseDTO::fromEntity);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> getMovie(@PathVariable String id) {
        Movie movie = movieService.findById(id);
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(MovieResponseDTO.fromEntity(movie));
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
            @Valid @ModelAttribute MovieRequestDTO request,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        // 1️⃣ FORMAT validate (Bean Validation) → trả lỗi ngay nếu có
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(toErrorMap(bindingResult));
        }

        // 2️⃣ Map DTO → entity
        Movie movie = new Movie();
        movie.setMovieID(movieService.generateMovieID());
        movie.setTitle(request.title().trim());
        movie.setGenre(request.genre());
        movie.setSummary(request.summary() != null ? request.summary().trim() : "");
        movie.setDuration(request.duration());
        movie.setReleaseDate(request.releaseDate());
        movie.setAgeRating(request.ageRating());
        movie.setTrailer(request.trailer() != null ? request.trailer().trim() : "");
        movie.setImg(image != null && !image.isEmpty() ? saveImage(image) : "");

        // 3️⃣ BUSINESS validate + save (trong service)
        Map<String, String> errors = movieService.createMovieBusiness(movie);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(MovieResponseDTO.fromEntity(movie));
    }

//    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> updateMovie(
//            @PathVariable String id,
//            @Valid @ModelAttribute MovieRequestDTO request,
//            BindingResult bindingResult,
//            @RequestParam(value = "image", required = false) MultipartFile image) {
//
//        Movie existing = movieService.findById(id);
//        if (existing == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        // 1️⃣ FORMAT validate (Bean Validation)
//        if (bindingResult.hasErrors()) {
//            return ResponseEntity.badRequest().body(toErrorMap(bindingResult));
//        }
//
//        // 2️⃣ Map DTO → entity (giữ ảnh cũ nếu không upload ảnh mới)
//        existing.setTitle(request.title().trim());
//        existing.setGenre(request.genre());
//        existing.setSummary(request.summary() != null ? request.summary().trim() : "");
//        existing.setDuration(request.duration());
//        existing.setReleaseDate(request.releaseDate());
//        existing.setAgeRating(request.ageRating());
//        existing.setTrailer(request.trailer() != null ? request.trailer().trim() : "");
//        if (image != null && !image.isEmpty()) {
//            existing.setImg(saveImage(image));
//        }
//
//        // 3️⃣ BUSINESS validate + save (trong service)
//        Map<String, String> errors = movieService.updateMovieBusiness(existing);
//        if (!errors.isEmpty()) {
//            return ResponseEntity.badRequest().body(errors);
//        }
//
//        return ResponseEntity.ok(MovieResponseDTO.fromEntity(existing));
//    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> updateMovie(
            @PathVariable String id,
            @Valid @ModelAttribute MovieRequestDTO request,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        // ==========================================
        // 1. KIỂM TRA MOVIE CÓ TỒN TẠI KHÔNG
        // ==========================================

        Movie existing = movieService.findById(id);

        if (existing == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        // ==========================================
        // 2. FORMAT VALIDATION
        // ==========================================

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(toErrorMap(bindingResult));
        }

        // ==========================================
        // 3. TẠO OBJECT CHỨA DỮ LIỆU MỚI
        // ==========================================

        Movie movie = new Movie();

        movie.setMovieID(id);

        movie.setTitle(
                request.title().trim()
        );

        movie.setGenre(
                request.genre()
        );

        movie.setSummary(
                request.summary() != null
                        ? request.summary().trim()
                        : ""
        );

        movie.setDuration(
                request.duration()
        );

        movie.setReleaseDate(
                request.releaseDate()
        );

        movie.setAgeRating(
                request.ageRating()
        );

        movie.setTrailer(
                request.trailer() != null
                        ? request.trailer().trim()
                        : ""
        );

        // ==========================================
        // 4. XỬ LÝ ẢNH
        // ==========================================

        if (image != null && !image.isEmpty()) {

            movie.setImg(
                    saveImage(image)
            );

        } else {

            // Không upload ảnh mới
            // Giữ ảnh cũ
            movie.setImg(
                    existing.getImg()
            );
        }

        // ==========================================
        // 5. BUSINESS VALIDATION + SAVE
        // ==========================================

        Map<String, String> errors =
                movieService.updateMovieBusiness(movie);

        if (!errors.isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(errors);
        }

        // ==========================================
        // 6. RESPONSE
        // ==========================================

        Movie updatedMovie =
                movieService.findById(id);

        return ResponseEntity
                .ok(
                        MovieResponseDTO.fromEntity(
                                updatedMovie
                        )
                );
    }

    /**
     * Gom lỗi Bean Validation về dạng { field: message } (mỗi field 1 message)
     * để đồng nhất với shape lỗi mà frontend đang đọc.
     */
    private Map<String, String> toErrorMap(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return errors;
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

    @GetMapping("/coming-soon")
    public ResponseEntity<?> comingSoon(
            @RequestParam(name = "page", defaultValue = "1") int page) {

        int size = 8;

        int pageIndex = page - 1;
        Pageable pageable = PageRequest.of(pageIndex, size);

        Page<Movie> comingSoonPage = movieService.findComingSoonMovies(pageable);

        int totalPages = comingSoonPage.getTotalPages();
        int currentPage = page;

        int visiblePages = 5;
        int startPage, endPage;

        if (totalPages <= visiblePages) {
            startPage = 1;
            endPage = totalPages;
        } else {
            startPage = ((currentPage - 1) / visiblePages) * visiblePages + 1;
            endPage = Math.min(startPage + visiblePages - 1, totalPages);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("movies", comingSoonPage.getContent());
        response.put("currentPage", currentPage);
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
    }
}
