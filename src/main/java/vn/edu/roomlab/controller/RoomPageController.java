package vn.edu.roomlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import vn.edu.roomlab.service.RoomService;

@Controller
public class RoomPageController {

    private final RoomService roomService;

    public RoomPageController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/rooms")
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
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