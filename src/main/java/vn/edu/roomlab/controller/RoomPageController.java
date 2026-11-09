package vn.edu.roomlab.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.edu.roomlab.dto.EquipmentResponse;
import vn.edu.roomlab.entity.Room;
import vn.edu.roomlab.service.EquipmentService;
import vn.edu.roomlab.service.RoomService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class RoomPageController {

    private final RoomService roomService;
    private final EquipmentService equipmentService;

    public RoomPageController(
            RoomService roomService,
            EquipmentService equipmentService) {
        this.roomService = roomService;
        this.equipmentService = equipmentService;
    }

    @GetMapping("/rooms")
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());

        Map<Integer, List<EquipmentResponse>> brokenEquipmentsByRoom =
                equipmentService.getAllBrokenEquipments().stream()
                        .collect(Collectors.groupingBy(EquipmentResponse::roomId));

        model.addAttribute("brokenEquipmentsByRoom", brokenEquipmentsByRoom);
        model.addAttribute("pageTitle", "Danh sách phòng");
        model.addAttribute(
                "pageDescription",
                "Xem thông tin và tình trạng hoạt động của các phòng máy."
        );
        model.addAttribute("activePage", "rooms");
        model.addAttribute("content", "fragments/rooms");

        return "layout";
    }

    @GetMapping("/rooms/{id}")
    public String roomDetail(@PathVariable Integer id, Model model) {
        model.addAttribute("room", roomService.getRoomById(id));
        model.addAttribute(
                "brokenEquipments",
                equipmentService.getBrokenEquipmentsByRoomId(id)
        );
        model.addAttribute("pageTitle", "Chi tiết phòng");
        model.addAttribute(
                "pageDescription",
                "Thông tin chi tiết về phòng máy trong hệ thống."
        );
        model.addAttribute("activePage", "rooms");
        model.addAttribute("content", "fragments/room-detail");

        return "layout";
    }

    @PostMapping("/admin/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public String createRoom(
            @RequestParam String roomName,
            @RequestParam(required = false) String location,
            @RequestParam Integer capacity,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        try {
            Room room = new Room();
            room.setRoomName(roomName);
            room.setLocation(location);
            room.setCapacity(capacity);
            room.setDescription(description);
            room.setStatus("AVAILABLE");
            roomService.saveRoom(room);
            redirectAttributes.addFlashAttribute("roomSuccess", "Đã thêm phòng máy mới.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("roomError", ex.getMessage());
        }
        return "redirect:/rooms";
    }

    @PostMapping("/admin/rooms/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateRoomStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        try {
            roomService.updateRoomStatus(id, status);
            redirectAttributes.addFlashAttribute("roomSuccess", "Đã cập nhật trạng thái phòng.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("roomError", ex.getMessage());
        }
        return "redirect:/rooms";
    }
}
