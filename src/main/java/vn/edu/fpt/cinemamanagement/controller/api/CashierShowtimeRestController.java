package vn.edu.fpt.cinemamanagement.controller.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.cinemamanagement.entities.Movie;
import vn.edu.fpt.cinemamanagement.entities.Showtime;
import vn.edu.fpt.cinemamanagement.entities.ShowtimeSeat;
import vn.edu.fpt.cinemamanagement.services.CashierShowTimeSeatService;
import vn.edu.fpt.cinemamanagement.services.ShowtimeService;
import vn.edu.fpt.cinemamanagement.services.TimeSlotService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cashier")
public class CashierShowtimeRestController {

    private final ShowtimeService showtimeService;
    private final TimeSlotService timeSlotService;
    private final CashierShowTimeSeatService cashierShowTimeSeatService;

    public CashierShowtimeRestController(ShowtimeService showtimeService,
                                           TimeSlotService timeSlotService,
                                           CashierShowTimeSeatService cashierShowTimeSeatService) {
        this.showtimeService = showtimeService;
        this.timeSlotService = timeSlotService;
        this.cashierShowTimeSeatService = cashierShowTimeSeatService;
    }

    /**
     * REST thay cho StaffHomeController#showShowtimesForCashier
     */
    @GetMapping("/showtimes")
    public ResponseEntity<?> getShowtimesForCashier(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        if (date == null) date = LocalDate.now();

        int pageIndex = Math.max(page, 1) - 1;
        int pageSize = Math.max(size, 1);

        var pageable = org.springframework.data.domain.PageRequest.of(pageIndex, pageSize);
        var showtimePage = showtimeService.getShowtimesPageByDateForCashier(date, pageable);
        List<Showtime> showtimes = showtimePage.getContent();

        // group theo movie -> room -> slots từ content đã paginate
        Map<String, List<Map<String, Object>>> scheduleGroups = new LinkedHashMap<>();
        for (Showtime st : showtimes) {
            String movieId = st.getMovie().getMovieID();

            String tempRoomName = "Unknown Room";
            if (st.getRoom() != null && st.getRoom().getTemplate() != null) {
                tempRoomName = st.getRoom().getTemplate().getName();
            } else if (st.getRoom() != null) {
                tempRoomName = st.getRoom().getId();
            }

            final String roomName = tempRoomName;
            scheduleGroups.putIfAbsent(movieId, new java.util.ArrayList<>());
            List<Map<String, Object>> roomList = scheduleGroups.get(movieId);

            Map<String, Object> roomGroup = roomList.stream()
                    .filter(r -> r.get("roomName").equals(roomName))
                    .findFirst()
                    .orElseGet(() -> {
                        Map<String, Object> newRoom = new java.util.HashMap<>();
                        newRoom.put("roomName", roomName);
                        newRoom.put("slots", new java.util.ArrayList<Map<String, Object>>());
                        roomList.add(newRoom);
                        return newRoom;
                    });

            List<Map<String, Object>> slots = (List<Map<String, Object>>) roomGroup.get("slots");
            Map<String, Object> slot = new java.util.HashMap<>();
            slot.put("showtimeId", st.getShowtimeId());
            slot.put("startTime", st.getStartTime());
            slot.put("endTime", st.getEndTime());
            slots.add(slot);
        }

        List<Movie> movieList = showtimes.stream()
                .map(Showtime::getMovie)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<LocalDate> days = timeSlotService.getWeekDates(date);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("days", days);
        payload.put("selectedDate", date);
        payload.put("movieList", movieList);
        payload.put("scheduleGroups", scheduleGroups);
        payload.put("prevDate", date.minusDays(1));
        payload.put("nextDate", date.plusDays(1));

        payload.put("currentPage", page);
        payload.put("totalPages", showtimePage.getTotalPages());
        payload.put("totalItems", showtimePage.getTotalElements());
        payload.put("pageSize", pageSize);

        return ResponseEntity.ok(payload);
    }


//    /**
//     * REST thay cho StaffHomeController#showSeatMap
//     */
//    @GetMapping("/booking/{movieId}")
//    public ResponseEntity<?> getSeatMapForCashier(
//            @PathVariable String movieId,
//            @RequestParam String time,
//            @RequestParam String date
//    ) {
//        Showtime showtime = showtimeService.findByMovie(movieId, time, date);
//        if (showtime == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        String showtimeId = showtime.getShowtimeId();
//        List<ShowtimeSeat> showtimeSeats = cashierShowTimeSeatService.createShowtimeSeats(showtimeId);
//
//        Map<String, String> seatStatusMap = showtimeSeats.stream()
//                .collect(Collectors.toMap(
//                        s -> s.getTemplateSeat().getId(),
//                        ShowtimeSeat::getStatus
//                ));
//
//        Map<String, List<ShowtimeSeat>> groupedSeats = showtimeSeats.stream()
//                .sorted((a, b) -> Integer.compare(a.getTemplateSeat().getSeatNumber(), b.getTemplateSeat().getSeatNumber()))
//                .collect(Collectors.groupingBy(
//                        s -> s.getTemplateSeat().getRowLabel(),
//                        LinkedHashMap::new,
//                        Collectors.toList()
//                ));
//
//        Map<String, Object> payload = new LinkedHashMap<>();
//        payload.put("groupSeat", groupedSeats);
//        payload.put("template", showtime.getRoom().getTemplate().getId());
//        payload.put("showtime", showtime);
//        payload.put("seatStatusMap", seatStatusMap);
//
//        return ResponseEntity.ok(payload);
//    }
}

