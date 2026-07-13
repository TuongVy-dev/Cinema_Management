package vn.edu.fpt.cinemamanagement.services;

import vn.edu.fpt.cinemamanagement.enums.SeatType;

import java.math.BigDecimal;

public interface ISeatPricingService {

    BigDecimal calculatePrice(SeatType seatType);
}
