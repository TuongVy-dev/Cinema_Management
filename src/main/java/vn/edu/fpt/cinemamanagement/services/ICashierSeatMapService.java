package vn.edu.fpt.cinemamanagement.services;
import vn.edu.fpt.cinemamanagement.dto.response.CashierSeatMapResponseDTO;
public interface ICashierSeatMapService {
    CashierSeatMapResponseDTO getSeatMap(String showtimeId);

}
