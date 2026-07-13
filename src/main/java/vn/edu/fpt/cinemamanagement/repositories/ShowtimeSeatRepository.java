package vn.edu.fpt.cinemamanagement.repositories;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.cinemamanagement.entities.ShowtimeSeat;

import java.util.List;

@Repository
public interface ShowtimeSeatRepository extends JpaRepository<ShowtimeSeat, String> {

    @Query(value = "SELECT TOP 1 showtime_seat_id FROM Showtime_Seat ORDER BY showtime_seat_id DESC", nativeQuery = true)
    String findLastId();

    List<ShowtimeSeat> getAllByShowtime_ShowtimeId(String showtimeShowtimeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT ss FROM ShowtimeSeat ss
        WHERE ss.showtime.showtimeId = :showtimeId
          AND ss.showtimeSeatID IN :showtimeSeatIds
    """)
    List<ShowtimeSeat> findByShowtimeIdAndIdsForUpdate(
            @Param("showtimeId") String showtimeId,
            @Param("showtimeSeatIds") List<String> showtimeSeatIds
    );

    @Query("""
        SELECT ss FROM ShowtimeSeat ss
        JOIN ss.templateSeat ts
        WHERE ss.showtime.showtimeId = :showtimeId
          AND ts.rowLabel = :rowLabel
          AND ts.seatNumber = :seatNumber
    """)
    ShowtimeSeat findSeatInShowtime(
            @Param("showtimeId") String showtimeId,
            @Param("rowLabel") String rowLabel,
            @Param("seatNumber") int seatNumber
    );
}
