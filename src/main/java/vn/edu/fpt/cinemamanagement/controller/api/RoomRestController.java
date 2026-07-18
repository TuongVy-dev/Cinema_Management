package vn.edu.fpt.cinemamanagement.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.dto.response.RoomResponseDTO;
import vn.edu.fpt.cinemamanagement.dto.response.SeatTemplateResponseDTO;
import vn.edu.fpt.cinemamanagement.services.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoomRestController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> listRooms() {
        return ResponseEntity.ok(roomService.getRoomResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getRoom(@PathVariable String id) {
        try {
            return ResponseEntity.ok(roomService.getRoomResponseById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<SeatTemplateResponseDTO> showTemplateSeat(@PathVariable String id) {
        try {
            return ResponseEntity.ok(roomService.getSeatTemplate(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
