package vn.edu.fpt.cinemamanagement.dto.response;

import java.math.BigDecimal;

public record ConcessionResponse(
        String concessionId,
        String name,
        BigDecimal price,
        String description,
        String img
) {
}
