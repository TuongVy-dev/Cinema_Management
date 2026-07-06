package vn.edu.fpt.cinemamanagement.dto.response;

import vn.edu.fpt.cinemamanagement.entities.Room;

/**
 * Response payload để trả thông tin room ra ngoài REST API (view rooms).
 * Tương ứng các cột trong thiết kế room_list.html:
 * Code (roomId) - Name (name) - Type (templateName) - Seats (totalSeats) - Status.
 */
public record RoomResponseDTO(
        String roomId,
        String name,
        String templateName,
        long totalSeats,
        String status
) {
    /**
     * Dựng DTO từ entity + các giá trị đã được service tính sẵn
     * (tên phòng hiển thị và tổng số ghế của template).
     */
    public static RoomResponseDTO of(Room room, String name, long totalSeats) {
        return new RoomResponseDTO(
                room.getId(),
                name,
                room.getTemplate() != null ? room.getTemplate().getName() : null,
                totalSeats,
                room.getStatus()
        );
    }
}
