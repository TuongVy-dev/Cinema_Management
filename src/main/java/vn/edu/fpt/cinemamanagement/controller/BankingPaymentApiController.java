package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.cinemamanagement.dto.BankingPaymentResponseDTO;
import vn.edu.fpt.cinemamanagement.entities.Booking;
import vn.edu.fpt.cinemamanagement.entities.Payment;
import vn.edu.fpt.cinemamanagement.entities.Staff;
import vn.edu.fpt.cinemamanagement.repositories.BookingRepository;
import vn.edu.fpt.cinemamanagement.repositories.PaymentRepository;
import vn.edu.fpt.cinemamanagement.repositories.StaffRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class BankingPaymentApiController {

    private static final String PAYMENT_SUCCESS_URL = "https://unquivering-latrice-semisentimental.ngrok-free.dev/payments/paymentsuccess?pay=";

    private final BookingRepository bookingRepository;
    private final StaffRepository staffRepository;
    private final PaymentRepository paymentRepository;

    public BankingPaymentApiController(BookingRepository bookingRepository,
                                       StaffRepository staffRepository,
                                       PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.staffRepository = staffRepository;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/ebanking/{bookingId}/{staffId}")
    public ResponseEntity<?> createCashierBankingPayment(@PathVariable String bookingId,
                                                         @PathVariable String staffId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            return notFound("BOOKING_NOT_FOUND", "Booking not found.");
        }

        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null) {
            return notFound("STAFF_NOT_FOUND", "Staff not found.");
        }

        Payment payment = createPendingBankingPayment(booking, staff);
        return ResponseEntity.ok(toBankingPaymentResponse(payment));
    }

    @PostMapping("/ebanking/{bookingId}")
    public ResponseEntity<?> createCustomerBankingPayment(@PathVariable String bookingId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            return notFound("BOOKING_NOT_FOUND", "Booking not found.");
        }

        Payment payment = createPendingBankingPayment(booking, null);
        return ResponseEntity.ok(toBankingPaymentResponse(payment));
    }

    private Payment createPendingBankingPayment(Booking booking, Staff staff) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString().substring(0, 8));
        payment.setBooking(booking);
        payment.setPaymentMethod("E-Banking");
        payment.setPaymentStatus("Pending");
        payment.setPaymentTime(LocalDateTime.now());
        payment.setAmount(booking.getTotalAmount());
        payment.setStaff(staff);

        return paymentRepository.save(payment);
    }

    private BankingPaymentResponseDTO toBankingPaymentResponse(Payment payment) {
        String qrContent = PAYMENT_SUCCESS_URL + payment.getId();
        String qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=500x500&data="
                + URLEncoder.encode(qrContent, StandardCharsets.UTF_8);

        return new BankingPaymentResponseDTO(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getStaff() != null ? payment.getStaff().getStaffID() : null,
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                qrContent,
                qrImageUrl
        );
    }

    private ResponseEntity<?> notFound(String code, String message) {
        return ResponseEntity.status(404).body(Map.of(
                "error", Map.of(
                        "code", code,
                        "message", message
                )
        ));
    }
}
