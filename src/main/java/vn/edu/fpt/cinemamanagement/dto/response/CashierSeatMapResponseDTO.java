package vn.edu.fpt.cinemamanagement.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CashierSeatMapResponseDTO {
    private String showtimeId;
    private String movieId;
    private String movieTitle;
    private String roomId;
    private String templateId;
    private LocalDate showDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<SeatDTO> seats = new ArrayList<>();

    public CashierSeatMapResponseDTO() {
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public List<SeatDTO> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDTO> seats) {
        this.seats = seats;
    }

    public static class SeatDTO {
        private String showtimeSeatId;
        private String templateSeatId;
        private String rowLabel;
        private int seatNumber;
        private String seatCode;
        private String seatType;
        private String status;
        private BigDecimal price;

        public SeatDTO() {
        }

        public String getShowtimeSeatId() {
            return showtimeSeatId;
        }

        public void setShowtimeSeatId(String showtimeSeatId) {
            this.showtimeSeatId = showtimeSeatId;
        }

        public String getTemplateSeatId() {
            return templateSeatId;
        }

        public void setTemplateSeatId(String templateSeatId) {
            this.templateSeatId = templateSeatId;
        }

        public String getRowLabel() {
            return rowLabel;
        }

        public void setRowLabel(String rowLabel) {
            this.rowLabel = rowLabel;
        }

        public int getSeatNumber() {
            return seatNumber;
        }

        public void setSeatNumber(int seatNumber) {
            this.seatNumber = seatNumber;
        }

        public String getSeatCode() {
            return seatCode;
        }

        public void setSeatCode(String seatCode) {
            this.seatCode = seatCode;
        }

        public String getSeatType() {
            return seatType;
        }

        public void setSeatType(String seatType) {
            this.seatType = seatType;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}