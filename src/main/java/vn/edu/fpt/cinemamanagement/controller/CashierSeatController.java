package vn.edu.fpt.cinemamanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.cinemamanagement.entities.Room;
import vn.edu.fpt.cinemamanagement.entities.TemplateSeat;
import vn.edu.fpt.cinemamanagement.repositories.RoomRepository;
import vn.edu.fpt.cinemamanagement.services.TemplateSeatService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cashier")
public class CashierSeatController {

    private final TemplateSeatService templateSeatService;
    private final RoomRepository roomRepository;

    public CashierSeatController(TemplateSeatService templateSeatService, RoomRepository roomRepository) {
        this.templateSeatService = templateSeatService;
        this.roomRepository = roomRepository;
    }

    /**
     * Display seat map for cashier to select seats
     * @param roomId - Room ID to display seats for
     * @param model - Spring Model
     * @return cashier seat map view
     */
    @GetMapping("/seat-map")
    public String showSeatMap(@RequestParam(required = false, defaultValue = "R001") String roomId, 
                              Model model) {
        
        // Get room information
        Room room = roomRepository.findById(roomId).orElse(null);
        
        if (room == null || room.getTemplate() == null) {
            model.addAttribute("error", "Room not found or has no template assigned");
            return "error/404";
        }

        String templateId = room.getTemplate().getId();
        
        // Get all seats for this template
        List<TemplateSeat> seats = templateSeatService.findAllSeatsByTemplateID(templateId);
        
        // Group seats by row for easier rendering
        Map<String, List<TemplateSeat>> groupedSeats = seats.stream()
                .collect(Collectors.groupingBy(
                        TemplateSeat::getRow_label,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        
        // Sort seats within each row by seat number
        groupedSeats.forEach((row, seatList) -> 
            seatList.sort((s1, s2) -> Integer.compare(s1.getSeat_number(), s2.getSeat_number()))
        );
        
        // Add attributes to model
        model.addAttribute("room", room);
        model.addAttribute("template", templateId);
        model.addAttribute("groupSeat", groupedSeats);
        model.addAttribute("totalSeats", seats.size());
        
        // Mock data for demonstration - in real app, this would come from showtime/movie
        model.addAttribute("movieTitle", "The Last Guardian");
        model.addAttribute("showtime", "7:30 PM");
        model.addAttribute("date", "December 15, 2024");
        model.addAttribute("roomName", room.getId());
        
        // Pricing information
        model.addAttribute("standardPrice", 10.00);
        model.addAttribute("vipPrice", 15.00);
        
        return "seats/cashier_seat_map";
    }

    /**
     * Get seat availability status (for AJAX calls)
     * @param roomId - Room ID
     * @return JSON response with seat availability
     */
    @GetMapping("/seat-availability")
    @ResponseBody
    public Map<String, Object> getSeatAvailability(@RequestParam String roomId) {
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            Room room = roomRepository.findById(roomId).orElse(null);
            
            if (room == null || room.getTemplate() == null) {
                response.put("success", false);
                response.put("message", "Room not found");
                return response;
            }
            
            String templateId = room.getTemplate().getId();
            List<TemplateSeat> seats = templateSeatService.findAllSeatsByTemplateID(templateId);
            
            // In a real application, you would check against actual bookings
            // For now, we'll return all seats as available
            List<Map<String, Object>> seatData = seats.stream()
                    .map(seat -> {
                        Map<String, Object> seatInfo = new LinkedHashMap<>();
                        seatInfo.put("seatId", seat.getRow_label() + seat.getSeat_number());
                        seatInfo.put("status", "available"); // In real app: check booking status
                        seatInfo.put("type", seat.getSeat_type());
                        return seatInfo;
                    })
                    .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("seats", seatData);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching seat availability: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * Process seat booking
     * @param bookingData - Booking information
     * @return JSON response with booking result
     */
    @PostMapping("/book-seats")
    @ResponseBody
    public Map<String, Object> bookSeats(@RequestBody Map<String, Object> bookingData) {
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            // Extract booking data
            String roomId = (String) bookingData.get("roomId");
            List<String> selectedSeats = (List<String>) bookingData.get("selectedSeats");
            String customerName = (String) bookingData.get("customerName");
            String customerPhone = (String) bookingData.get("customerPhone");
            String customerEmail = (String) bookingData.get("customerEmail");
            Double totalAmount = ((Number) bookingData.get("totalAmount")).doubleValue();
            
            // Validate input
            if (selectedSeats == null || selectedSeats.isEmpty()) {
                response.put("success", false);
                response.put("message", "No seats selected");
                return response;
            }
            
            if (customerName == null || customerName.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Customer name is required");
                return response;
            }
            
            // In a real application, you would:
            // 1. Check seat availability again
            // 2. Create booking record
            // 3. Create ticket records
            // 4. Process payment
            // 5. Send confirmation email
            
            // For now, return success
            response.put("success", true);
            response.put("message", "Booking successful");
            response.put("bookingId", "BK" + System.currentTimeMillis());
            response.put("selectedSeats", selectedSeats);
            response.put("totalAmount", totalAmount);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing booking: " + e.getMessage());
        }
        
        return response;
    }
}
