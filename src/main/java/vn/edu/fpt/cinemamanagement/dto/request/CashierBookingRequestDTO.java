package vn.edu.fpt.cinemamanagement.dto.request;

import java.util.ArrayList;
import java.util.List;

public class CashierBookingRequestDTO {
    private String showtimeId;
    private List<String> showtimeSeatIds = new ArrayList<>();
    private List<ConcessionItemDTO> concessions = new ArrayList<>();

    public CashierBookingRequestDTO() {
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public List<String> getShowtimeSeatIds() {
        return showtimeSeatIds;
    }

    public void setShowtimeSeatIds(List<String> showtimeSeatIds) {
        this.showtimeSeatIds = showtimeSeatIds;
    }

    public List<ConcessionItemDTO> getConcessions() {
        return concessions;
    }

    public void setConcessions(List<ConcessionItemDTO> concessions) {
        this.concessions = concessions;
    }


    public static class ConcessionItemDTO {
        private String concessionId;
        private int quantity;

        public ConcessionItemDTO() {
        }

        public String getConcessionId() {
            return concessionId;
        }

        public void setConcessionId(String concessionId) {
            this.concessionId = concessionId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}