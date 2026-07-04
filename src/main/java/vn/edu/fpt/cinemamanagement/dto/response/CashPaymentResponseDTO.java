package vn.edu.fpt.cinemamanagement.dto.response;

public class CashPaymentResponseDTO {
    private String paymentId;
    private String bookingId;
    private long amountPaid;
    private long change;
    private String ticketId;

    public CashPaymentResponseDTO() {}

    public CashPaymentResponseDTO(String paymentId, String bookingId, long amountPaid, long change, String ticketId) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amountPaid = amountPaid;
        this.change = change;
        this.ticketId = ticketId;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public long getAmountPaid() { return amountPaid; }
    public void setAmountPaid(long amountPaid) { this.amountPaid = amountPaid; }
    public long getChange() { return change; }
    public void setChange(long change) { this.change = change; }
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
}
