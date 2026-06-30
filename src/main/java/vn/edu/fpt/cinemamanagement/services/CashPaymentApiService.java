package vn.edu.fpt.cinemamanagement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.cinemamanagement.entities.*;
import vn.edu.fpt.cinemamanagement.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CashPaymentApiService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingDetailRepository bookingDetailRepository;
    @Autowired
    private ShowtimeSeatRepository showtimeSeatRepository;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private PaymentService paymentService; // to use generatePaymentId if available

    @Transactional
    public Map<String, Object> processCashPayment(String bookingId, long cashGiven, Staff staff) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found.");
        }
        if ("PAID".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Booking is already paid.");
        }

        long total = booking.getTotalAmount().longValue();
        if (cashGiven < total) {
            throw new IllegalArgumentException("Customer payment is less than total amount. Required: " + total + ", Given: " + cashGiven);
        }
        long change = cashGiven - total;

        // 1. Create Payment
        Payment payment = new Payment();
        payment.setId(paymentService.generatePaymentId());
        payment.setBooking(booking);
        payment.setPaymentMethod("CASH");
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentStatus("PAID");
        payment.setStaff(staff);
        paymentRepository.save(payment);

        // 2. Update Booking status
        booking.setStatus("PAID");
        bookingRepository.save(booking);

        // 3. Update ShowtimeSeat to unavailable
        List<BookingDetail> details = bookingDetailRepository.findByBooking(booking);
        for (BookingDetail d : details) {
            ShowtimeSeat seat = d.getShowtimeSeat();
            if (seat != null) {
                seat.setStatus("unavailable");
                showtimeSeatRepository.save(seat);
            }
        }

        // 4. Create Ticket
        Ticket ticket = new Ticket();
        ticket.setId(ticketService.generateTicketId());
        ticket.setBooking(booking);
        ticket.setPrice(payment.getAmount());
        ticket.setRedemptionStatus(true);
        ticket.setCheckedInTime(LocalDateTime.now());
        ticket.setRedemptionStaff(staff);
        ticketService.saveTicket(ticket);

        // 5. Return result
        Map<String, Object> result = new HashMap<>();
        result.put("paymentId", payment.getId());
        result.put("bookingId", bookingId);
        result.put("amountPaid", total);
        result.put("change", change);
        result.put("ticketId", ticket.getId());
        return result;
    }
}
