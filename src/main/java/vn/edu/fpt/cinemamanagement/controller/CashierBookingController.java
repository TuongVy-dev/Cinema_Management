package vn.edu.fpt.cinemamanagement.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.dto.request.CashierBookingRequestDTO;
import vn.edu.fpt.cinemamanagement.dto.response.CashierBookingResponseDTO;
import vn.edu.fpt.cinemamanagement.dto.response.CashierSeatMapResponseDTO;
import vn.edu.fpt.cinemamanagement.services.CashierBookingService;
import vn.edu.fpt.cinemamanagement.services.ICashierSeatMapService;

import java.util.Map;

@RestController
@RequestMapping("/api/cashier")
public class CashierBookingController {
    private final ICashierSeatMapService cashierSeatMapService;
    private final CashierBookingService cashierBookingService;

    public CashierBookingController(
            ICashierSeatMapService cashierSeatMapService,
            CashierBookingService cashierBookingService
    ) {
        this.cashierSeatMapService = cashierSeatMapService;
        this.cashierBookingService = cashierBookingService;
    }

    @GetMapping("/showtimes/{showtimeId}/seats")
    public ResponseEntity<?> getSeatMap(@PathVariable String showtimeId) {
        try {
            CashierSeatMapResponseDTO response = cashierSeatMapService.getSeatMap(showtimeId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@RequestBody CashierBookingRequestDTO request) {
        try {
            // Cashier walk-in customer mặc định
            CashierBookingResponseDTO response =
                    cashierBookingService.createBooking(request, "KH000000");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String bookingId) {
        try {
            cashierBookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
