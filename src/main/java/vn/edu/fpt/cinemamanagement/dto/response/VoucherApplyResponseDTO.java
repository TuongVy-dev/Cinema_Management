package vn.edu.fpt.cinemamanagement.dto.response;

public class VoucherApplyResponseDTO {
    private String voucherId;
    private String discountType;
    private int discountValue;
    private double originalPrice;
    private double discountedPrice;

    public VoucherApplyResponseDTO() {}

    public VoucherApplyResponseDTO(String voucherId, String discountType, int discountValue, double originalPrice, double discountedPrice) {
        this.voucherId = voucherId;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
    }

    public String getVoucherId() { return voucherId; }
    public void setVoucherId(String voucherId) { this.voucherId = voucherId; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    public int getDiscountValue() { return discountValue; }
    public void setDiscountValue(int discountValue) { this.discountValue = discountValue; }
    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }
    public double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }
}
