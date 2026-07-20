package vn.edu.fpt.cinemamanagement.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.cinemamanagement.dto.ShowtimeResponseDTO;
import vn.edu.fpt.cinemamanagement.dto.ShowtimeResponseDetailDTO;
import vn.edu.fpt.cinemamanagement.entities.Movie;
import vn.edu.fpt.cinemamanagement.entities.Room;
import vn.edu.fpt.cinemamanagement.entities.Showtime;
import vn.edu.fpt.cinemamanagement.entities.Template;
import vn.edu.fpt.cinemamanagement.repositories.BookingDetailRepository;
import vn.edu.fpt.cinemamanagement.repositories.ShowtimeRepository;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShowtimeService {

    private final ShowtimeRepository repo;
    private final MovieService movieService;
    private final RoomService roomService;
    private final TimeSlotService timeSlotService;
    private final BookingDetailRepository bookingDetailRepository;

    public ShowtimeService(ShowtimeRepository repo,
                           MovieService movieService,
                           RoomService roomService,
                           TimeSlotService timeSlotService,
                           BookingDetailRepository bookingDetailRepository) {
        this.repo = repo;
        this.movieService = movieService;
        this.roomService = roomService;
        this.timeSlotService = timeSlotService;
        this.bookingDetailRepository = bookingDetailRepository;
    }


    public void validateShowtime(String showtimeId, String movieId, String roomId, LocalDate showDate, LocalTime startTime) {
        Movie movie = movieService.findById(movieId);
        Room room = roomService.findById(roomId);
        if (movie == null || room == null)
            throw new IllegalArgumentException("Movie or Room not found.");

        LocalDate now = LocalDate.now();

        if (showDate.isBefore(now.minusMonths(1))) {
            throw new IllegalArgumentException("Showtime date cannot be more than 1 months in the past.");
        }
        if (showDate.isAfter(now.plusMonths(1))) {
            throw new IllegalArgumentException("Showtime date cannot be more than 1 months in the future.");
        }
        if (showDate.isBefore(now.minusYears(1))) {
            throw new IllegalArgumentException("Showtime date cannot be more than 1 year in the past.");
        }
        if (showDate.isAfter(now.plusYears(1))) {
            throw new IllegalArgumentException("Showtime date cannot be more than 1 year in the future.");
        }

        int duration = movie.getDuration();
        int roundedDuration = (int) (Math.ceil(duration / 5.0) * 5);
        LocalTime endTime = startTime.plusMinutes(roundedDuration);

        boolean overlap;
        boolean sameMovieOtherRoom;
        if (showtimeId != null && !showtimeId.isEmpty()) {
            overlap = repo.hasOverlapInRoomExcludingShowtime(roomId, showDate, startTime, endTime, showtimeId);
            sameMovieOtherRoom = repo.hasSameMovieInOtherRoomExcludingShowtime(movieId, roomId, showDate, startTime, endTime, showtimeId);
        } else {
            overlap = repo.hasOverlapInRoom(roomId, showDate, startTime, endTime);
            sameMovieOtherRoom = repo.hasSameMovieInOtherRoom(movieId, roomId, showDate, startTime, endTime);
        }

        if (overlap) {
            throw new IllegalArgumentException(String.format("There is already a showtime at %s. Please choose another time slot.", startTime.toString()));
        }
        if (sameMovieOtherRoom) {
            throw new IllegalArgumentException("This movie is already scheduled in another room at this time. Please choose a different time or movie.");
        }
    }

    @Transactional
    public Showtime createShowtime(String movieId, String roomId, LocalDate showDate, LocalTime startTime) {
        validateShowtime(null, movieId, roomId, showDate, startTime);
        Movie movie = movieService.findById(movieId);
        Room room = roomService.findById(roomId);

        int duration = movie.getDuration();
        int roundedDuration = (int) (Math.ceil(duration / 5.0) * 5);
        LocalTime endTime = startTime.plusMinutes(roundedDuration);

        Showtime st = new Showtime();
        st.setShowtimeId(generateShowtimeId());
        st.setMovie(movie);
        st.setRoom(room);
        st.setShowDate(showDate);
        st.setStartTime(startTime);
        st.setEndTime(endTime);
        return repo.save(st);
    }

    private String generateShowtimeId() {
        String lastId = repo.findTopByOrderByShowtimeIdDesc()
                .map(Showtime::getShowtimeId)
                .orElse(null);

        int next = 1;
        if (lastId != null && lastId.startsWith("SW")) {
            try {
                next = Integer.parseInt(lastId.substring(2)) + 1;
            } catch (NumberFormatException ignored) {}
        }

        return String.format("SW%05d", next);
    }

    public List<Showtime> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Page<ShowtimeResponseDTO> getAdminShowtimes(Pageable pageable) {
        return repo.findAllForAdmin(pageable).map(this::toShowtimeResponseDTO);
    }

    @Transactional(readOnly = true)
    public Optional<ShowtimeResponseDetailDTO> getAdminShowtimeDetail(String showtimeId) {
        return repo.findDetailByShowtimeId(showtimeId).map(this::toShowtimeResponseDetailDTO);
    }

    private ShowtimeResponseDTO toShowtimeResponseDTO(Showtime showtime) {
        Movie movie = showtime.getMovie();
        Room room = showtime.getRoom();
        Template template = room != null ? room.getTemplate() : null;

        return new ShowtimeResponseDTO(
                showtime.getShowtimeId(),
                movie != null ? movie.getMovieID() : null,
                movie != null ? movie.getTitle() : null,
                room != null ? room.getId() : null,
                template != null ? template.getName() : null,
                showtime.getShowDate(),
                showtime.getStartTime(),
                showtime.getEndTime(),
                showtime.getDisplayStatus()
        );
    }

    private ShowtimeResponseDetailDTO toShowtimeResponseDetailDTO(Showtime showtime) {
        Movie movie = showtime.getMovie();
        Room room = showtime.getRoom();
        Template template = room != null ? room.getTemplate() : null;

        return new ShowtimeResponseDetailDTO(
                showtime.getShowtimeId(),
                movie != null ? movie.getMovieID() : null,
                movie != null ? movie.getTitle() : null,
                movie != null ? movie.getGenre() : null,
                movie != null ? movie.getDuration() : null,
                room != null ? room.getId() : null,
                template != null ? template.getName() : null,
                room != null ? room.getStatus() : null,
                showtime.getShowDate(),
                showtime.getStartTime(),
                showtime.getEndTime(),
                showtime.getDisplayStatus()
        );
    }


    public List<Movie> getMoviesByDate(LocalDate date) {
        List<Showtime> shows = repo.findByShowDate(date);
        return shows.stream()
                .map(Showtime::getMovie)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<Showtime> getShowtimesPageByDateForCashier(LocalDate date, Pageable pageable) {
        return repo.findByShowDateForCashier(date, pageable);
    }

    public List<Showtime> getShowtimesByDate(LocalDate date) {
        List<Showtime> showtimes = repo.findByShowDate(date);


        showtimes.forEach(st -> {
            if (st.getRoom() != null && st.getRoom().getTemplate() != null) {
                st.getRoom().getTemplate().getName();
            }
        });

        return showtimes;
    }

    public List<Room> getAllRoomsWithTemplate() {
        List<Room> rooms = roomService.getAllRooms(); // hoặc roomService.findAll()
        for (Room r : rooms) {
            if (r.getTemplate() != null) {
                r.getTemplate().getName(); // ép Hibernate load template
            }
            System.out.println("ROOM DEBUG: " + r.getId() +
                    " → template = " + (r.getTemplate() != null ? r.getTemplate().getName() : "null"));
        }
        return rooms;
    }



    @Transactional
    public Showtime updateShowtime(String showtimeId, String movieId, String roomId,
                                   LocalDate showDate, LocalTime startTime) {

        Showtime existing = repo.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found."));

        validateShowtime(showtimeId, movieId, roomId, showDate, startTime);

        Movie movie = movieService.findById(movieId);
        Room room = roomService.findById(roomId);
        int duration = movie.getDuration();
        int roundedDuration = (int) (Math.ceil(duration / 5.0) * 5);
        LocalTime endTime = startTime.plusMinutes(roundedDuration);

        existing.setMovie(movie);
        existing.setRoom(room);
        existing.setShowDate(showDate);
        existing.setStartTime(startTime);
        existing.setEndTime(endTime);

        return repo.save(existing);
    }

    /**  Gom nhóm suất chiếu theo phim → phòng → các khung giờ, hiển thị đúng template name */
    public Map<String, List<Map<String, Object>>> getGroupedShowtimes(LocalDate date) {
        List<Showtime> showtimes = repo.findByShowDate(date);
        Map<String, List<Map<String, Object>>> grouped = new LinkedHashMap<>();

        for (Showtime st : showtimes) {
            String movieId = st.getMovie().getMovieID();

            //  Lấy đúng tên template của phòng
            String tempRoomName = "Unknown Room";
            if (st.getRoom() != null && st.getRoom().getTemplate() != null) {
                tempRoomName = st.getRoom().getTemplate().getName();
            } else if (st.getRoom() != null) {
                tempRoomName = st.getRoom().getId(); // fallback nếu template null
            }

            //  Phải gán thành final để dùng trong lambda
            final String roomName = tempRoomName;

            grouped.putIfAbsent(movieId, new ArrayList<>());
            List<Map<String, Object>> roomList = grouped.get(movieId);

            Map<String, Object> roomGroup = roomList.stream()
                    .filter(r -> r.get("roomName").equals(roomName))
                    .findFirst()
                    .orElseGet(() -> {
                        Map<String, Object> newRoom = new HashMap<>();
                        newRoom.put("roomName", roomName);
                        newRoom.put("slots", new ArrayList<Map<String, Object>>());
                        roomList.add(newRoom);
                        return newRoom;
                    });

            List<Map<String, Object>> slots = (List<Map<String, Object>>) roomGroup.get("slots");
            Map<String, Object> slot = new HashMap<>();
            slot.put("startTime", st.getStartTime());
            slot.put("endTime", st.getEndTime());
            slots.add(slot);
        }
        return grouped;
    }

    public Showtime showtimeByID(String id){
        return repo.findByShowtimeId(id);
    }


    public List<Showtime> getShowtimesByMovieAndDate(String movieId, LocalDate selectedDate) {
        return repo.findAllByMovie_MovieIDAndShowDate(movieId, selectedDate);
    }
    public Showtime findByMovie(String movieId , String times, String  dates){
        LocalDate date= LocalDate.parse(dates);
        LocalTime time = LocalTime.parse(times, DateTimeFormatter.ofPattern("HH:mm"));
        return repo.findMovieAndDateTime(movieId, date, time);
    }

    @Transactional
    public void deleteShowtime(String showtimeId) {
        Showtime showtime = repo.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found"));

        // Hibernate sẽ tự xóa tất cả seat liên quan nhờ cascade
        repo.delete(showtime);
    }


    public List<Showtime> getShowtimesByDateRange(LocalDate start, LocalDate end) {
        return repo.findByShowDateBetween(start, end);
    }

}
