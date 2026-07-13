package vn.edu.fpt.cinemamanagement.services;

import vn.edu.fpt.cinemamanagement.dto.RoomDetailDTO;
import vn.edu.fpt.cinemamanagement.dto.response.RoomResponseDTO;
import vn.edu.fpt.cinemamanagement.dto.response.SeatTemplateResponseDTO;
import vn.edu.fpt.cinemamanagement.entities.Room;
import vn.edu.fpt.cinemamanagement.entities.Template;
import vn.edu.fpt.cinemamanagement.entities.TemplateSeat;
import vn.edu.fpt.cinemamanagement.repositories.RoomRepository;
import vn.edu.fpt.cinemamanagement.repositories.TemplateSeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private RoomRepository roomRepository;
    private TemplateSeatRepository templateSeatRepository;
    private TemplateSeatService templateSeatService;

    public RoomService(
            RoomRepository roomRepository,
            TemplateSeatRepository templateSeatRepository,
            TemplateSeatService templateSeatService) {
        this.roomRepository = roomRepository;
        this.templateSeatRepository = templateSeatRepository;
        this.templateSeatService = templateSeatService;
    }

 public List<Room> getAllRooms(){
return roomRepository.findAll();
 }
    // ============================================
    // REST API - View Rooms
    // ============================================

    /** Danh sách room cho REST API (mirror kiến trúc của Movie). */
    @Transactional
    public List<RoomResponseDTO> getRoomResponses() {
        return roomRepository.findAll().stream()
                .map(this::toRoomResponse)
                .collect(Collectors.toList());
    }

    /** Lấy 1 room theo id cho REST API. */
    @Transactional
    public RoomResponseDTO getRoomResponseById(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng có ID: " + roomId));
        return toRoomResponse(room);
    }

    private RoomResponseDTO toRoomResponse(Room room) {
        Template template = room.getTemplate();
        long totalSeats = template != null
                ? templateSeatService.countTotalSeatsByTemplateID(template.getId())
                : 0L;
        return RoomResponseDTO.of(room, buildRoomName(room.getId()), totalSeats);
    }

    /**
     * Sơ đồ ghế của template gắn với 1 room (cho màn View Seat Template).
     * Mirror logic của RoomController.showTemplateSeat: sort theo rowLabel +
     * seatNumber rồi gom nhóm theo hàng.
     */
    @Transactional
    public SeatTemplateResponseDTO getSeatTemplate(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng có ID: " + roomId));

        Template template = room.getTemplate();
        if (template == null) {
            throw new IllegalArgumentException("Phòng chưa được gán template: " + roomId);
        }

        List<TemplateSeat> seats = templateSeatService.findAllSeatsByTemplateID(template.getId());
        seats.sort(Comparator.comparing(TemplateSeat::getRowLabel)
                .thenComparing(TemplateSeat::getSeatNumber));

        // Gom nhóm theo hàng, giữ thứ tự hàng (LinkedHashMap)
        Map<String, List<SeatTemplateResponseDTO.Seat>> grouped = new LinkedHashMap<>();
        for (TemplateSeat s : seats) {
            grouped.computeIfAbsent(s.getRowLabel(), k -> new ArrayList<>())
                    .add(new SeatTemplateResponseDTO.Seat(
                            s.getRowLabel(),
                            s.getSeatNumber(),
                            s.getSeatType(),
                            s.getRowLabel() + s.getSeatNumber()));
        }

        List<SeatTemplateResponseDTO.Row> rows = grouped.entrySet().stream()
                .map(e -> new SeatTemplateResponseDTO.Row(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new SeatTemplateResponseDTO(room.getId(), template.getId(), rows);
    }

    @Transactional
    public List<RoomDetailDTO> getRoomDetails() {
        return roomRepository.findAll().stream()
                .map(room -> {
                    String templateName = room.getTemplate().getName();
                    String templateId = room.getTemplate().getId();
                    Long totalSeats = (long) templateSeatService.countTotalSeatsByTemplateID(templateId);

                    // Tạo DTO
                    RoomDetailDTO dto = new RoomDetailDTO(
                            room.getId(),
                            templateName,
                            totalSeats,
                            room.getStatus()
                    );
                    dto.setBuildRoomName(buildRoomName(room.getId()));

                    return dto;
                })
                .collect(Collectors.toList());
    }
 @Transactional
public String getRoomId(String roomID){
        Room room = roomRepository.findById(roomID).orElseThrow(()->new IllegalArgumentException("Room Not Found"));
        return room.getId();
}
@Transactional
    public Room findById(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng: " + id));
    }
    @Transactional
    public String getRoomStatus(String roomID) {
        Room r = roomRepository.findById(roomID)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng: " + roomID));
        return r.getStatus(); // "active" / "unactive"
    }

    @Transactional
    public String buildRoomName(String id) {
        if (id == null || id.isEmpty()) return "";
        String upper = id.toUpperCase();
        try {
            if (upper.startsWith("R")) {
                return "Room " + Integer.parseInt(id.substring(1));
            }
        } catch (NumberFormatException ignored) {}
        return "Room " + id; // fallback
    }
    @Transactional
    public Room updateRoom(Room room) {
        Room existingRoom = roomRepository.findById(room.getId())
                .orElseThrow(() -> new IllegalArgumentException("Phòng không tồn tại: " + room.getId()));

        if (room.getTemplate() != null) {
            existingRoom.setTemplate(room.getTemplate());
        }
        if (room.getStatus() != null) {
            existingRoom.setStatus(room.getStatus());
        }

        return roomRepository.save(existingRoom);
    }
    @Transactional
    public Room createRoom(Room room) {
        if (roomRepository.existsById(room.getId())) {
            throw new IllegalArgumentException("Phòng với ID " + room.getId() + " đã tồn tại");
        }
        if (room.getTemplate() == null) {
            throw new IllegalArgumentException("Template không được để trống");
        }
        return roomRepository.save(room);
    }
    @Transactional
    public void deleteRoom(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Phòng không tồn tại: " + roomId));
        roomRepository.delete(room);
    }
    public RoomDetailDTO getRoomDetailById(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng có ID: " + roomId));

        Template template = room.getTemplate();


        RoomDetailDTO dto = new RoomDetailDTO();
        dto.setRoomId(room.getId());
        dto.setTemplateName(template.getName());
        dto.setStatus(room.getStatus());

        return dto;
    }


}
