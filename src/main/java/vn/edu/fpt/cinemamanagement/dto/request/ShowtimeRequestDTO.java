package vn.edu.fpt.cinemamanagement.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public class ShowtimeRequestDTO {
    private String movieId;
    private String roomId;
    private LocalDate showDate;
    private LocalTime startTime;

    public ShowtimeRequestDTO() {}

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public LocalDate getShowDate() { return showDate; }
    public void setShowDate(LocalDate showDate) { this.showDate = showDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
}
