package vn.edu.fpt.cinemamanagement.dto.response;

import java.util.List;

/**
 * Response payload cho màn View Seat Template.
 * Ghế đã được service sắp xếp theo hàng (rowLabel) rồi tới số ghế (seatNumber),
 * và gom nhóm theo từng hàng — khớp với cấu trúc groupSeat của seat_template.html.
 */
public record SeatTemplateResponseDTO(
        String roomId,
        String templateId,
        List<Row> rows
) {
    /** Một hàng ghế (theo rowLabel). */
    public record Row(String rowLabel, List<Seat> seats) {
    }

    /** Một ghế trong sơ đồ. label = rowLabel + seatNumber (vd "A1"). */
    public record Seat(String rowLabel, int seatNumber, String seatType, String label) {
    }
}
