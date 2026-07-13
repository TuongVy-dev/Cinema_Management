package vn.edu.fpt.cinemamanagement.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.fpt.cinemamanagement.entities.Concession;
import vn.edu.fpt.cinemamanagement.entities.Showtime;
import vn.edu.fpt.cinemamanagement.entities.ShowtimeSeat;
import vn.edu.fpt.cinemamanagement.enums.SeatStatus;
import vn.edu.fpt.cinemamanagement.repositories.ConcessionRepository;
import vn.edu.fpt.cinemamanagement.repositories.ShowtimeSeatRepository;
import vn.edu.fpt.cinemamanagement.entities.TemplateSeat;
import vn.edu.fpt.cinemamanagement.services.TemplateSeatService;
import java.util.stream.Collectors;

import java.util.List;

@Service
public class CashierShowTimeSeatService {
    @Autowired
    private ShowtimeSeatRepository showtimeSeatRepository;
    @Autowired
    private ConcessionRepository concessionRepository;
    @Autowired
    private ShowtimeSeatService showtimeSeatService;
    @Autowired
    private ShowtimeService showtimeService;
    @Autowired
    private TemplateSeatService templateSeatService;

    @Transactional
    public List<ShowtimeSeat> createShowtimeSeats(String showtimeId) {
        Showtime showtime = showtimeService.showtimeByID(showtimeId);
        String templateId = showtime.getRoom().getTemplate().getId();

        // 1. Lấy danh sách ghế hiện tại trong DB
        List<ShowtimeSeat> currentSeats = showtimeSeatRepository.getAllByShowtime_ShowtimeId(showtimeId);

        // 2. Nếu chưa có ghế nào -> Tạo mới hoàn toàn
        if (currentSeats.isEmpty()) {
            showtimeSeatService.createShowtimeSeats(showtimeId, templateId);
            return showtimeSeatRepository.getAllByShowtime_ShowtimeId(showtimeId);
        }

        // 3. Kiểm tra xem template có thay đổi không (khác ID)
        String currentTemplateIdOfSeats = currentSeats.get(0).getTemplateSeat().getTemplate().getId();
        if (!currentTemplateIdOfSeats.equals(templateId)) {
            // Khác template -> Xóa hết tạo lại
            showtimeSeatRepository.deleteAll(currentSeats);
            showtimeSeatService.createShowtimeSeats(showtimeId, templateId);
            return showtimeSeatRepository.getAllByShowtime_ShowtimeId(showtimeId);
        }

        // 4. Cùng template ID -> Đồng bộ hóa (Sync)
        List<TemplateSeat> templateSeats = templateSeatService.findAllSeatsByTemplateID(templateId);

        // a. Tìm các ghế cần thêm mới (có trong template nhưng chưa có trong showtime)
        List<String> currentTemplateSeatIds = currentSeats.stream()
                .map(s -> s.getTemplateSeat().getId())
                .collect(Collectors.toList());

        for (TemplateSeat ts : templateSeats) {
            if (!currentTemplateSeatIds.contains(ts.getId())) {
                showtimeSeatService.createSingleShowtimeSeat(showtimeId, ts);
            }
        }

        // b. Tìm các ghế cần xóa (có trong showtime nhưng không còn trong template)
        List<String> activeTemplateSeatIds = templateSeats.stream()
                .map(TemplateSeat::getId)
                .collect(Collectors.toList());

        List<ShowtimeSeat> seatsToDelete = currentSeats.stream()
                .filter(s -> !activeTemplateSeatIds.contains(s.getTemplateSeat().getId()))
                .collect(Collectors.toList());

        if (!seatsToDelete.isEmpty()) {
            showtimeSeatRepository.deleteAll(seatsToDelete);
        }

        // Trả về danh sách mới nhất
        return showtimeSeatRepository.getAllByShowtime_ShowtimeId(showtimeId);
    }

    /**
     * Khi nhấn Payment → đổi các ghế được chọn thành PENDING
     */
//    @Transactional
//    public void changeSeatStatus(String showtimeId, List<String> selectedSeatCodes) {
//        List<ShowtimeSeat> seats = showtimeSeatRepository.getAllByShowtime_ShowtimeId(showtimeId);
//
//        for (ShowtimeSeat seat : seats) {
//            String code = seat.getTemplateSeat().getRowLabel() + seat.getTemplateSeat().getSeatNumber();
//            if (selectedSeatCodes.contains(code) && "available".equals(seat.getStatus())) {
//                seat.setStatus("pending");
//            }
//        }
//
//        showtimeSeatRepository.saveAll(seats);
//    }

    /**
     * Khi thanh toán thành công → đổi PENDING → UNAVAILABLE
     */
    @Transactional
    public void confirmPayment(String showtimeId, List<String> selectedSeatCodes) {
        List<ShowtimeSeat> seats = showtimeSeatRepository.getAllByShowtime_ShowtimeId(showtimeId);

        for (ShowtimeSeat seat : seats) {
            String code = seat.getTemplateSeat().getRowLabel() + seat.getTemplateSeat().getSeatNumber();
            if (selectedSeatCodes.contains(code) && SeatStatus.PENDING.equals(seat.getStatus())) {
                seat.setStatus(SeatStatus.UNAVAILABLE);
            }
        }

        showtimeSeatRepository.saveAll(seats);
    }

    /**
     * Khi countdown hết mà chưa thanh toán → trả lại AVAILABLE
     */
    @Transactional
    public void releaseStatusSeats(String showtimeId) {
        List<ShowtimeSeat> seats = showtimeSeatRepository.getAllByShowtime_ShowtimeId(showtimeId);
        for (ShowtimeSeat seat : seats) {
            if (SeatStatus.PENDING.equals(seat.getStatus())) {
                seat.setStatus(SeatStatus.AVAILABLE);
            }
        }
        showtimeSeatRepository.saveAll(seats);
    }
    @Transactional
    public List<Concession> findAll()
    {
        return concessionRepository.findAll();
    }


}

