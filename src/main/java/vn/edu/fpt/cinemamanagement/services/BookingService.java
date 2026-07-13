package vn.edu.fpt.cinemamanagement.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import vn.edu.fpt.cinemamanagement.entities.*;
import vn.edu.fpt.cinemamanagement.enums.BookingStatus;
import vn.edu.fpt.cinemamanagement.enums.SeatStatus;
import vn.edu.fpt.cinemamanagement.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private BookingDetailRepository bookingDetailRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private ConcessionRepository concessionRepository;
    @Autowired
    private ShowtimeSeatRepository showtimeSeatRepository;
    @Autowired
    TemplateSeatRepository templateSeatRepository;
    @Autowired
    private VoucherRepository voucherRepository;

    // ðŸ”¹ HÃ m sinh ID cho BookingDetail
    private String generateBookingDetailId() {
        BookingDetail lastDetail = bookingDetailRepository.findTopByOrderByBookingDetailIdDesc();
        if (lastDetail == null) {
            return "BD000001";
        }
        int lastNum = Integer.parseInt(lastDetail.getBookingDetailId().substring(2)) + 1;
        return String.format("BD%06d", lastNum);
    }

    @Transactional
    public Booking createBooking(String showtimeId,List<String> seatIds, List<String> concessionIds, List<String> qtyList, String userId) {

        // --- (1) Táº¡o Booking ---
        Booking booking = new Booking();
        Booking lastBooking = bookingRepository.findTopByOrderByIdDesc();
        String newId = (lastBooking == null)
                ? "BK000001"
                : String.format("BK%06d", Integer.parseInt(lastBooking.getId().substring(2)) + 1);

        booking.setId(newId);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUserId(userId);
        booking = bookingRepository.saveAndFlush(booking);

        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal seatPrice = new BigDecimal("80000");

        System.out.println("=== START CREATE BOOKING ===");
        System.out.println("Seat IDs: " + seatIds);
        System.out.println("Concession IDs: " + concessionIds + " | qtyList: " + qtyList);

        // --- (2) LÆ°u chi tiáº¿t GHáº¾ ---
        if (seatIds != null) {
            for (String seatLabel : seatIds) { // C2, C3, ...
                String row = seatLabel.substring(0, 1);
                int number = Integer.parseInt(seatLabel.substring(1));

                // ðŸ”¹ TÃ¬m gháº¿ Ä‘Ãºng trong suáº¥t chiáº¿u Ä‘Ã³
                ShowtimeSeat showtimeSeat = showtimeSeatRepository.findSeatInShowtime(showtimeId, row, number);

                if (showtimeSeat != null) {
                    BookingDetail detail = new BookingDetail();
                    detail.setBookingDetailId(generateBookingDetailId());
                    detail.setBooking(booking);
                    detail.setItemType("seat");
                    detail.setShowtimeSeat(showtimeSeat);
                    detail.setQuantity(1);
                    detail.setUnitPrice(seatPrice);
                    detail.setTotalPrice(seatPrice);
                    bookingDetailRepository.save(detail);

                    // cáº­p nháº­t tráº¡ng thÃ¡i gháº¿
                    showtimeSeat.setStatus(SeatStatus.PENDING);
                    showtimeSeatRepository.saveAndFlush(showtimeSeat);

                    totalPrice = totalPrice.add(seatPrice);
                    System.out.println("âœ… Added seat: " + seatLabel + " | Price: " + seatPrice);
                } else {
                    System.out.println("âš ï¸ Seat not found for showtimeId=" + showtimeId + " seat=" + seatLabel);
                }
            }
        }


        // --- (3) LÆ°u chi tiáº¿t Äá»’ Ä‚N ---
        if (concessionIds != null && qtyList != null) {
            for (int i = 0; i < concessionIds.size(); i++) {
                String conId = concessionIds.get(i);
                int qty = Integer.parseInt(qtyList.get(i));

                Concession c = concessionRepository.findById(conId).orElse(null);
                if (c != null && qty > 0) {
                    BookingDetail detail = new BookingDetail();
                    detail.setBookingDetailId(generateBookingDetailId());
                    detail.setBooking(booking);
                    detail.setItemType("concession");
                    detail.setConcession(c);
                    detail.setQuantity(qty);
                    detail.setUnitPrice(c.getPrice());
                    detail.setTotalPrice(c.getPrice().multiply(BigDecimal.valueOf(qty)));
                    bookingDetailRepository.save(detail);

                    totalPrice = totalPrice.add(c.getPrice().multiply(BigDecimal.valueOf(qty)));
                    System.out.println("âœ… Added concession: " + conId + " x " + qty);
                }
            }
        }

        // --- (4) Cáº­p nháº­t tá»•ng tiá»n ---
        booking.setTotalAmount(totalPrice);
        bookingRepository.saveAndFlush(booking);
        System.out.println("ðŸ’° TOTAL PRICE = " + totalPrice);

        return booking;
    }

    @Transactional
    public Booking applyVoucherAndUpdateTotal(Booking booking, double finalTotal, String voucherCode) {
        // cáº­p nháº­t tá»•ng tiá»n Ä‘Ã£ giáº£m
        booking.setTotalAmount(BigDecimal.valueOf(finalTotal));

        // náº¿u muá»‘n lÆ°u thÃ´ng tin voucher vÃ o booking vÃ  tÄƒng used_count
        if (voucherCode != null && !voucherCode.isBlank()) {
            booking.setTotalAmount(BigDecimal.valueOf(finalTotal));
            if (voucherCode != null && !voucherCode.isBlank()) {
                Voucher v = voucherRepository.findByVoucherCode(voucherCode);
                if (v != null) {
                    v.setUsedCount(v.getUsedCount() + 1);
                    voucherRepository.save(v);
                }
            }
        }

        // lÆ°u láº¡i booking vá»›i tá»•ng tiá»n má»›i
        return bookingRepository.save(booking);
    }

    public List<Voucher> getAvailableVouchers() {
        return voucherRepository.findAvailableVouchers();
    }
    public Booking findById(String id) {
        return bookingRepository.findById(id).orElse(null);
    }



    public Booking getReceipt() {
       Booking booking = bookingRepository.findTopByOrderByIdDesc();
       return booking;
    }

    public List<BookingDetail> getBookingDetail(String id) {
        List<BookingDetail> detail = bookingDetailRepository.findByBookingId(id);
        return detail;
    }

    public Booking findBookingById(String id) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        return booking;
    }




}
