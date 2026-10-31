
package vn.edu.roomlab.controller;

import org.springframework.web.bind.annotation.*;
import vn.edu.roomlab.entity.Room;
import vn.edu.roomlab.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getAll() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public Room getById(@PathVariable Integer id) {
        return roomService.getRoomById(id);
    }

    @PostMapping
    public Room create(@RequestBody Room room) {
        return roomService.saveRoom(room);
    }

    @PutMapping("/{id}")
    public Room update(@PathVariable Integer id,
                       @RequestBody Room room) {
        room.setRoomId(id);
        return roomService.saveRoom(room);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        roomService.deleteRoom(id);
    }
}
