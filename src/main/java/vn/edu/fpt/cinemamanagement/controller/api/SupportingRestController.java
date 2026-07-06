package vn.edu.fpt.cinemamanagement.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.entities.Booking;
import vn.edu.fpt.cinemamanagement.entities.BookingDetail;
import vn.edu.fpt.cinemamanagement.entities.Movie;
import vn.edu.fpt.cinemamanagement.entities.Room;
import vn.edu.fpt.cinemamanagement.services.BookingService;
import vn.edu.fpt.cinemamanagement.services.MovieService;
import vn.edu.fpt.cinemamanagement.services.ShowtimeService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SupportingRestController {

    private final MovieService movieService;
    private final ShowtimeService showtimeService;
    private final BookingService bookingService;

    public SupportingRestController(MovieService movieService,
                                    ShowtimeService showtimeService,
                                    BookingService bookingService) {
        this.movieService = movieService;
        this.showtimeService = showtimeService;
        this.bookingService = bookingService;
    }

    // ========================== MOVIES NOW SHOWING ==========================
    @GetMapping("/public/movies/now-showing")
    public ResponseEntity<?> getNowShowingMovies() {
        List<Movie> movies = movieService.getNowShowingMovies();
        List<Map<String, Object>> result = movies.stream().map(m -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("movieId", m.getMovieID());
            map.put("title", m.getTitle());
            map.put("duration", m.getDuration());
            map.put("genre", m.getGenre());
            map.put("releaseDate", m.getReleaseDate() != null ? m.getReleaseDate().toString() : null);
            map.put("img", m.getImg());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ========================== ROOMS ==========================
    @GetMapping("/admin/rooms")
    public ResponseEntity<?> getRooms() {
        List<Room> rooms = showtimeService.getAllRoomsWithTemplate();
        List<Map<String, Object>> result = rooms.stream().map(r -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("roomId", r.getId());
            map.put("templateName", r.getTemplate() != null ? r.getTemplate().getName() : "Unknown");
            map.put("status", r.getStatus());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ========================== BOOKING DETAIL (for cashier) ==========================
    @GetMapping("/cashier/bookings/{id}")
    public ResponseEntity<?> getBookingDetail(@PathVariable String id) {
        Booking booking = bookingService.findById(id);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }

        List<BookingDetail> details = bookingService.getBookingDetail(id);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("bookingId", booking.getId());
        result.put("userId", booking.getUserId());
        result.put("status", booking.getStatus());
        result.put("totalAmount", booking.getTotalAmount());
        result.put("createdAt", booking.getCreatedAt() != null ? booking.getCreatedAt().toString() : null);

        List<Map<String, Object>> detailList = details.stream().map(d -> {
            Map<String, Object> dMap = new LinkedHashMap<>();
            dMap.put("detailId", d.getBookingDetailId());
            dMap.put("itemType", d.getItemType());
            dMap.put("quantity", d.getQuantity());
            dMap.put("unitPrice", d.getUnitPrice());
            if (d.getShowtimeSeat() != null && d.getShowtimeSeat().getTemplateSeat() != null) {
                dMap.put("seatLabel", d.getShowtimeSeat().getTemplateSeat().getRowLabel()
                        + d.getShowtimeSeat().getTemplateSeat().getSeatNumber());
            }
            if (d.getConcession() != null) {
                dMap.put("concessionName", d.getConcession().getName());
            }
            return dMap;
        }).collect(Collectors.toList());
        result.put("details", detailList);

        return ResponseEntity.ok(result);
    }
}
