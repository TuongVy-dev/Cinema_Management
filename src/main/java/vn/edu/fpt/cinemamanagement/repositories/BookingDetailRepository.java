package vn.edu.fpt.cinemamanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.cinemamanagement.entities.Booking;
import vn.edu.fpt.cinemamanagement.entities.BookingDetail;

import java.util.List;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, String> {
    BookingDetail findTopByOrderByBookingDetailIdDesc();
    List<BookingDetail> findByBookingId(String bookingId);

    List<BookingDetail> findByBooking(Booking booking);

    // Check whether a showtime is used in any booking detail.
    // If ANY booking_detail references a ShowtimeSeat that belongs to this Showtime => do not delete the showtime.
    boolean existsByShowtimeSeat_Showtime_ShowtimeId(String showtimeId);
}

