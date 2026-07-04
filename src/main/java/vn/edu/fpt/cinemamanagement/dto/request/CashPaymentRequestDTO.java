package vn.edu.fpt.cinemamanagement.dto.request;

public class CashPaymentRequestDTO {
    private String bookingId;
    private long cashGiven;

    public CashPaymentRequestDTO() {}

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public long getCashGiven() { return cashGiven; }
    public void setCashGiven(long cashGiven) { this.cashGiven = cashGiven; }
}
