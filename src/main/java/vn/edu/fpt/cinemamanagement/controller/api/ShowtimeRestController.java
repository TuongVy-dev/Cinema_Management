package vn.edu.fpt.cinemamanagement.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.dto.request.ShowtimeRequestDTO;
import vn.edu.fpt.cinemamanagement.entities.Showtime;
import vn.edu.fpt.cinemamanagement.services.ShowtimeService;
import vn.edu.fpt.cinemamanagement.services.ShowtimeSeatService;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.cinemamanagement.dto.ShowtimeResponseDTO;

@RestController
@RequestMapping("/api/admin/showtimes")
public class ShowtimeRestController {

    private final ShowtimeService showtimeService;
    private final ShowtimeSeatService showtimeSeatService;

    public ShowtimeRestController(ShowtimeService showtimeService,
                                  ShowtimeSeatService showtimeSeatService) {
        this.showtimeService = showtimeService;
        this.showtimeSeatService = showtimeSeatService;
    }

    // ========================== GET SHOWTIME LIST ==========================
    @GetMapping
    public ResponseEntity<Page<ShowtimeResponseDTO>> getShowtimes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(showtimeService.getAdminShowtimes(pageable));
    }

    // ========================== VALIDATE SHOWTIME ==========================
    @PostMapping("/validate")
    public ResponseEntity<?> validateShowtime(@RequestBody Map<String, String> payload) {
        String movieId = payload.get("movieId");
        String roomId = payload.get("roomId");
        String showDateStr = payload.get("showDate");
        String startTimeStr = payload.get("startTime");
        String showtimeId = payload.get("showtimeId");

        if (movieId == null || roomId == null || showDateStr == null || startTimeStr == null ||
            movieId.isEmpty() || roomId.isEmpty() || showDateStr.isEmpty() || startTimeStr.isEmpty()) {
            return ResponseEntity.ok(Map.of("valid", true));
        }

        try {
            java.time.LocalDate showDate = java.time.LocalDate.parse(showDateStr);
            java.time.LocalTime startTime = java.time.LocalTime.parse(startTimeStr);
            showtimeService.validateShowtime(showtimeId, movieId, roomId, showDate, startTime);
            return ResponseEntity.ok(Map.of("valid", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(Map.of("valid", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false, "message", "Invalid format"));
        }
    }

    // ========================== CREATE SHOWTIME ==========================
    @PostMapping
    public ResponseEntity<?> createShowtime(@RequestBody ShowtimeRequestDTO dto) {
        try {
            Showtime show = showtimeService.createShowtime(
                    dto.getMovieId(),
                    dto.getRoomId(),
                    dto.getShowDate(),
                    dto.getStartTime()
            );
            // Create showtime seats after creating the showtime
            if (show != null) {
                showtimeSeatService.createShowtimeSeats(
                        show.getShowtimeId(),
                        show.getRoom().getTemplate().getId()
                );
            }
            return ResponseEntity.ok(Map.of(
                    "message", "Showtime created successfully!",
                    "showtimeId", show.getShowtimeId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ========================== UPDATE SHOWTIME ==========================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShowtime(@PathVariable String id,
                                            @RequestBody ShowtimeRequestDTO dto) {
        try {
            Showtime updated = showtimeService.updateShowtime(
                    id,
                    dto.getMovieId(),
                    dto.getRoomId(),
                    dto.getShowDate(),
                    dto.getStartTime()
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Showtime updated successfully!",
                    "showtimeId", updated.getShowtimeId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ========================== GET SHOWTIME DETAIL ==========================
    @GetMapping("/{id}")
    public ResponseEntity<?> getShowtimeDetail(@PathVariable String id) {
        Showtime showtime = showtimeService.showtimeByID(id);
        if (showtime == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of(
                "showtimeId", showtime.getShowtimeId(),
                "movieId", showtime.getMovie().getMovieID(),
                "movieTitle", showtime.getMovie().getTitle(),
                "movieDuration", showtime.getMovie().getDuration(),
                "roomId", showtime.getRoom().getId(),
                "showDate", showtime.getShowDate().toString(),
                "startTime", showtime.getStartTime().toString(),
                "endTime", showtime.getEndTime().toString()
        ));
    }
}
