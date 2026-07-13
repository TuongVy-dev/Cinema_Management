package vn.edu.fpt.cinemamanagement.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import vn.edu.fpt.cinemamanagement.dto.request.CashierBookingRequestDTO;
import vn.edu.fpt.cinemamanagement.dto.response.CashierBookingResponseDTO;
import vn.edu.fpt.cinemamanagement.entities.*;
import vn.edu.fpt.cinemamanagement.enums.BookingStatus;
import vn.edu.fpt.cinemamanagement.enums.SeatStatus;
import vn.edu.fpt.cinemamanagement.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CashierBookingService {
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final ShowtimeRepository showtimeRepository;
    private final ShowtimeSeatRepository showtimeSeatRepository;
    private final ConcessionRepository concessionRepository;
    private final ISeatPricingService seatPricingService;

    public CashierBookingService(
            BookingRepository bookingRepository,
            BookingDetailRepository bookingDetailRepository,
            ShowtimeRepository showtimeRepository,
            ShowtimeSeatRepository showtimeSeatRepository,
            ConcessionRepository concessionRepository,
            ISeatPricingService seatPricingService
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.showtimeRepository = showtimeRepository;
        this.showtimeSeatRepository = showtimeSeatRepository;
        this.concessionRepository = concessionRepository;
        this.seatPricingService = seatPricingService;
    }

    @Transactional
    public CashierBookingResponseDTO createBooking(
            CashierBookingRequestDTO request,
            String userId
    ) {
        if (request == null || request.getShowtimeId() == null || request.getShowtimeId().isBlank()) {
            throw new IllegalArgumentException("Showtime is required.");
        }

        List<String> requestedSeatIds = request.getShowtimeSeatIds();
        if (requestedSeatIds == null || requestedSeatIds.isEmpty()) {
            throw new IllegalArgumentException("At least one seat is required.");
        }

        Showtime showtime = showtimeRepository.findByShowtimeId(request.getShowtimeId());
        if (showtime == null) {
            throw new IllegalArgumentException("Showtime not found: " + request.getShowtimeId());
        }

        List<String> uniqueSeatIds = requestedSeatIds.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(id -> !id.isBlank())
                .distinct()
                .toList();

        if (uniqueSeatIds.size() != requestedSeatIds.size()) {
            throw new IllegalArgumentException("Invalid or duplicated seat ids.");
        }

        List<ShowtimeSeat> seats = showtimeSeatRepository.findByShowtimeIdAndIdsForUpdate(
                request.getShowtimeId(),
                uniqueSeatIds
        );

        if (seats.size() != uniqueSeatIds.size()) {
            throw new IllegalArgumentException("Some seats were not found in this showtime.");
        }

        for (ShowtimeSeat seat : seats) {
            if (!SeatStatus.AVAILABLE.equals(seat.getStatus())) {
                String seatCode = seat.getTemplateSeat().getRowLabel() + seat.getTemplateSeat().getSeatNumber();
                throw new IllegalArgumentException("Seat is not available: " + seatCode);
            }
        }

        Booking booking = new Booking();
        booking.setId(generateBookingId());
        booking.setUserId(userId);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setCreatedAt(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<BookingDetail> details = new ArrayList<>();
        List<CashierBookingResponseDTO.BookingItemDTO> responseItems = new ArrayList<>();
        int nextDetailNumber = nextBookingDetailNumber();

        for (ShowtimeSeat seat : seats) {
            BigDecimal unitPrice = seatPricingService.calculatePrice(seat.getTemplateSeat().getSeatType());

            BookingDetail detail = new BookingDetail();
            detail.setBookingDetailId(formatBookingDetailId(nextDetailNumber++));
            detail.setBooking(booking);
            detail.setItemType("SEAT");
            detail.setShowtimeSeat(seat);
            detail.setQuantity(1);
            detail.setUnitPrice(unitPrice);
            detail.setTotalPrice(unitPrice);
            details.add(detail);

            seat.setStatus(SeatStatus.PENDING);
            totalAmount = totalAmount.add(unitPrice);

            responseItems.add(toSeatItem(seat, unitPrice));
        }

        if (request.getConcessions() != null) {
            for (CashierBookingRequestDTO.ConcessionItemDTO item : request.getConcessions()) {
                if (item == null) {
                    throw new IllegalArgumentException("Concession item is required.");
                }

                if (item.getConcessionId() == null || item.getConcessionId().isBlank()) {
                    throw new IllegalArgumentException("Concession id is required.");
                }

                if (item.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Concession quantity must be greater than 0.");
                }

                Concession concession = concessionRepository.findById(item.getConcessionId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Concession not found: " + item.getConcessionId()
                        ));

                BigDecimal lineTotal = concession.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

                BookingDetail detail = new BookingDetail();
                detail.setBookingDetailId(formatBookingDetailId(nextDetailNumber++));
                detail.setBooking(booking);
                detail.setItemType("CONCESSION");
                detail.setConcession(concession);
                detail.setQuantity(item.getQuantity());
                detail.setUnitPrice(concession.getPrice());
                detail.setTotalPrice(lineTotal);
                details.add(detail);

                totalAmount = totalAmount.add(lineTotal);
                responseItems.add(toConcessionItem(concession, item.getQuantity()));
            }
        }

        booking.setTotalAmount(totalAmount);
        booking.setBookingDetails(details);

        Booking savedBooking = bookingRepository.save(booking);
        showtimeSeatRepository.saveAll(seats);

        return new CashierBookingResponseDTO(
                savedBooking.getId(),
                savedBooking.getStatus().name(),
                savedBooking.getTotalAmount(),
                savedBooking.getCreatedAt(),
                responseItems
        );
    }
    @Transactional
    public void cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (!BookingStatus.BOOKED.equals(booking.getStatus())) {
            throw new IllegalArgumentException("Only BOOKED bookings can be cancelled.");
        }

        List<BookingDetail> details = bookingDetailRepository.findByBooking(booking);
        List<ShowtimeSeat> releasedSeats = new ArrayList<>();

        for (BookingDetail detail : details) {
            ShowtimeSeat seat = detail.getShowtimeSeat();

            if (seat != null && SeatStatus.PENDING.equals(seat.getStatus())) {
                seat.setStatus(SeatStatus.AVAILABLE);
                releasedSeats.add(seat);
            }
        }

        booking.setStatus(BookingStatus.CANCELLED);

        showtimeSeatRepository.saveAll(releasedSeats);
        bookingRepository.save(booking);
    }

    private String generateBookingId() {
        Booking lastBooking = bookingRepository.findTopByOrderByIdDesc();
        if (lastBooking == null) {
            return "BK000001";
        }

        int nextNumber = Integer.parseInt(lastBooking.getId().substring(2)) + 1;
        return String.format("BK%06d", nextNumber);
    }

    private int nextBookingDetailNumber() {
        BookingDetail lastDetail = bookingDetailRepository.findTopByOrderByBookingDetailIdDesc();
        if (lastDetail == null) {
            return 1;
        }

        return Integer.parseInt(lastDetail.getBookingDetailId().substring(2)) + 1;
    }

    private String formatBookingDetailId(int number) {
        return String.format("BD%06d", number);
    }

    private CashierBookingResponseDTO.BookingItemDTO toSeatItem(ShowtimeSeat seat, BigDecimal unitPrice) {
        CashierBookingResponseDTO.BookingItemDTO item = new CashierBookingResponseDTO.BookingItemDTO();

        String seatCode = seat.getTemplateSeat().getRowLabel() + seat.getTemplateSeat().getSeatNumber();

        item.setItemType("SEAT");
        item.setItemId(seat.getShowtimeSeatID());
        item.setName(seatCode);
        item.setQuantity(1);
        item.setUnitPrice(unitPrice);
        item.setTotalPrice(unitPrice);

        return item;
    }

    private CashierBookingResponseDTO.BookingItemDTO toConcessionItem(Concession concession, int quantity) {
        CashierBookingResponseDTO.BookingItemDTO item = new CashierBookingResponseDTO.BookingItemDTO();

        BigDecimal totalPrice = concession.getPrice().multiply(BigDecimal.valueOf(quantity));

        item.setItemType("CONCESSION");
        item.setItemId(concession.getConcessionId());
        item.setName(concession.getName());
        item.setQuantity(quantity);
        item.setUnitPrice(concession.getPrice());
        item.setTotalPrice(totalPrice);

        return item;
    }

}
