package vn.edu.fpt.cinemamanagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class Voucher {
    @Id
    @Column(name = "voucher_id")
    private String voucherId;
    @Column(name = "voucher_name")
    private String voucherName;
    private String code;
    @Column(name = "discount_value")
    private Integer discountValue;
    @Column(name = "usage_limit")
    private Integer usageLimit;
    @Column(name = "used_count")
    private Integer usedCount;
    @Column(name = "discount_type")
    private String discountType;
    private boolean status;

    public Voucher() {}

    public Voucher(String voucherId, String voucherName, String code, Integer discountValue, Integer usageLimit, Integer usedCount, String discountType, boolean status) {
        this.voucherId = voucherId;
        this.voucherName = voucherName;
        this.code = code;
        this.discountValue = discountValue;
        this.usageLimit = usageLimit;
        this.usedCount = usedCount;
        this.discountType = discountType;
        this.status = status;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public String getVoucherName() {
        return voucherName;
    }

    public void setVoucherName(String voucherName) {
        this.voucherName = voucherName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Integer discountValue) {
        this.discountValue = discountValue;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
