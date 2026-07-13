package vn.edu.fpt.cinemamanagement.services.impl;
import org.springframework.stereotype.Service;
import vn.edu.fpt.cinemamanagement.enums.SeatType;
import vn.edu.fpt.cinemamanagement.services.ISeatPricingService;
import java.math.BigDecimal;
@Service
public class SeatPricingService implements ISeatPricingService {
    private static final BigDecimal STANDARD_PRICE =
            BigDecimal.valueOf(80_000);

    private static final BigDecimal VIP_PRICE =
            BigDecimal.valueOf(100_000);

    @Override
    public BigDecimal calculatePrice(SeatType seatType) {
        if (seatType == null) {
            throw new IllegalArgumentException("Seat type is required");
        }

        return switch (seatType) {
            case STANDARD -> STANDARD_PRICE;
            case VIP -> VIP_PRICE;
        };
    }
}
