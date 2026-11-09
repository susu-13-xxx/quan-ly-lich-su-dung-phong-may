package vn.edu.roomlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import vn.edu.roomlab.dto.EquipmentResponse;
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
}
