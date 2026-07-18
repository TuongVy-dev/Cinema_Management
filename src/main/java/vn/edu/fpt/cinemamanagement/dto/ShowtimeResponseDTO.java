package vn.edu.fpt.cinemamanagement.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShowtimeResponseDTO(String showtimeId,
                                  String movieId,
                                  String movieTitle,
                                  String roomId,
                                  String roomType,
                                  LocalDate showDate,
                                  LocalTime startTime,
                                  LocalTime endTime,
                                  String displayStatus) {
}
