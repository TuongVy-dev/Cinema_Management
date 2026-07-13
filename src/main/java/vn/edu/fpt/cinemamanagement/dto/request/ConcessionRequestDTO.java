package vn.edu.fpt.cinemamanagement.dto.request;
import java.math.BigDecimal;
public record ConcessionRequestDTO(
        String type,
        String name,
        BigDecimal price,
        String description,
        String img
) {
}
