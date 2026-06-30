package vn.edu.fpt.cinemamanagement.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.dto.request.CashPaymentRequestDTO;
import vn.edu.fpt.cinemamanagement.dto.response.CashPaymentResponseDTO;
import vn.edu.fpt.cinemamanagement.entities.Staff;
import vn.edu.fpt.cinemamanagement.repositories.StaffRepository;
import vn.edu.fpt.cinemamanagement.services.CashPaymentApiService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/cashier/payments")
public class PaymentRestController {

    private final CashPaymentApiService paymentService;
    private final StaffRepository staffRepository;

    public PaymentRestController(CashPaymentApiService paymentService, StaffRepository staffRepository) {
        this.paymentService = paymentService;
        this.staffRepository = staffRepository;
    }

    // ========================== CASH PAYMENT ==========================
    @PostMapping("/cash")
    public ResponseEntity<?> processCashPayment(@RequestBody CashPaymentRequestDTO dto,
                                                Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated."));
        }

        // Get staff from Principal (username) → StaffRepository.findByUsername
        String username = principal.getName();
        Staff staff = staffRepository.findByUsername(username).orElse(null);
        if (staff == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Staff not found for username: " + username));
        }

        try {
            Map<String, Object> result = paymentService.processCashPayment(
                    dto.getBookingId(), dto.getCashGiven(), staff
            );

            CashPaymentResponseDTO response = new CashPaymentResponseDTO(
                    (String) result.get("paymentId"),
                    (String) result.get("bookingId"),
                    (long) result.get("amountPaid"),
                    (long) result.get("change"),
                    (String) result.get("ticketId")
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
