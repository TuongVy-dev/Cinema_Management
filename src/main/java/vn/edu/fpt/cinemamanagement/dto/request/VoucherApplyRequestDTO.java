package vn.edu.fpt.cinemamanagement.dto.request;

public class VoucherApplyRequestDTO {
    private String code;
    private double totalPrice;

    public VoucherApplyRequestDTO() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}
