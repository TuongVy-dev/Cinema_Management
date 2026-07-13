package vn.edu.fpt.cinemamanagement.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CashierBookingResponseDTO {
    private String bookingId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<BookingItemDTO> items = new ArrayList<>();

    public CashierBookingResponseDTO() {
    }

    public CashierBookingResponseDTO(
            String bookingId,
            String status,
            BigDecimal totalAmount,
            LocalDateTime createdAt,
            List<BookingItemDTO> items
    ) {
        this.bookingId = bookingId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.items = items;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<BookingItemDTO> getItems() {
        return items;
    }

    public void setItems(List<BookingItemDTO> items) {
        this.items = items;
    }

    public static class BookingItemDTO {
        private String itemType;
        private String itemId;
        private String name;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;

        public BookingItemDTO() {
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }
    }
}