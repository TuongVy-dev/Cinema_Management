package vn.edu.fpt.cinemamanagement.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShowtimeResponseDetailDTO(String showtimeId,
                                        String movieId,
                                        String movieTitle,
                                        String movieGenre,
                                        Integer movieDuration,
                                        String roomId,
                                        String roomType,
                                        String roomStatus,
                                        LocalDate showDate,
                                        LocalTime startTime,
                                        LocalTime endTime,
                                        String displayStatus) {
}
