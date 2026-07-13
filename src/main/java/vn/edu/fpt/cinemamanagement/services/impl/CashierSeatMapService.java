package vn.edu.fpt.cinemamanagement.services.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import vn.edu.fpt.cinemamanagement.dto.response.CashierSeatMapResponseDTO;
import vn.edu.fpt.cinemamanagement.entities.Showtime;
import vn.edu.fpt.cinemamanagement.entities.ShowtimeSeat;
import vn.edu.fpt.cinemamanagement.entities.TemplateSeat;
import vn.edu.fpt.cinemamanagement.repositories.ShowtimeRepository;
import vn.edu.fpt.cinemamanagement.repositories.ShowtimeSeatRepository;
import vn.edu.fpt.cinemamanagement.services.CashierShowTimeSeatService;
import vn.edu.fpt.cinemamanagement.services.ICashierSeatMapService;
import vn.edu.fpt.cinemamanagement.services.ISeatPricingService;

import java.util.Comparator;
import java.util.List;

@Service
public class CashierSeatMapService implements ICashierSeatMapService {
    private final ShowtimeRepository showtimeRepository;
    private final ISeatPricingService seatPricingService;
    private final ShowtimeSeatRepository showtimeSeatRepository;

    public CashierSeatMapService(
            ShowtimeRepository showtimeRepository,
            ShowtimeSeatRepository showtimeSeatRepository,
            ISeatPricingService seatPricingService
    ) {
        this.showtimeRepository = showtimeRepository;
        this.showtimeSeatRepository = showtimeSeatRepository;
        this.seatPricingService = seatPricingService;
    }

    @Override
    @Transactional
    public CashierSeatMapResponseDTO getSeatMap(String showtimeId) {
        Showtime showtime = showtimeRepository.findByShowtimeId(showtimeId);

        if (showtime == null) {
            throw new IllegalArgumentException("Showtime not found: " + showtimeId);
        }
        List<ShowtimeSeat> showtimeSeats =
                showtimeSeatRepository.getAllByShowtime_ShowtimeId(showtimeId);

        if (showtimeSeats.isEmpty()) {
            throw new IllegalArgumentException("Showtime seats are not initialized for showtime: " + showtimeId);
        }

        showtimeSeats.sort(
                Comparator
                        .comparing((ShowtimeSeat seat) ->
                                seat.getTemplateSeat().getRowLabel())
                        .thenComparing(seat ->
                                seat.getTemplateSeat().getSeatNumber())
        );

        CashierSeatMapResponseDTO response = new CashierSeatMapResponseDTO();

        response.setShowtimeId(showtime.getShowtimeId());
        response.setMovieId(showtime.getMovie().getMovieID());
        response.setMovieTitle(showtime.getMovie().getTitle());
        response.setRoomId(showtime.getRoom().getId());
        response.setTemplateId(showtime.getRoom().getTemplate().getId());
        response.setShowDate(showtime.getShowDate());
        response.setStartTime(showtime.getStartTime());
        response.setEndTime(showtime.getEndTime());

        List<CashierSeatMapResponseDTO.SeatDTO> seatDTOs =
                showtimeSeats.stream()
                        .map(this::toSeatDTO)
                        .toList();

        response.setSeats(seatDTOs);

        return response;
    }

    private CashierSeatMapResponseDTO.SeatDTO toSeatDTO(ShowtimeSeat showtimeSeat) {
        TemplateSeat templateSeat = showtimeSeat.getTemplateSeat();

        String seatCode =
                templateSeat.getRowLabel() + templateSeat.getSeatNumber();

        CashierSeatMapResponseDTO.SeatDTO dto =
                new CashierSeatMapResponseDTO.SeatDTO();

        dto.setShowtimeSeatId(showtimeSeat.getShowtimeSeatID());
        dto.setTemplateSeatId(templateSeat.getId());
        dto.setRowLabel(templateSeat.getRowLabel());
        dto.setSeatNumber(templateSeat.getSeatNumber());
        dto.setSeatCode(seatCode);
        dto.setSeatType(templateSeat.getSeatType().name());
        dto.setStatus(showtimeSeat.getStatus().name());
        dto.setPrice(seatPricingService.calculatePrice(templateSeat.getSeatType()));

        return dto;
    }
}
