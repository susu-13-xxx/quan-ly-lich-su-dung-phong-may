
package vn.edu.roomlab.controller;

import org.springframework.web.bind.annotation.*;
import vn.edu.roomlab.entity.User;
import vn.edu.roomlab.dto.UserResponse;
import vn.edu.roomlab.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAllUserResponses();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Integer id) {
        return userService.getUserResponseById(id);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Integer id,
                       @RequestBody User user) {
        user.setUserId(id);
        return userService.saveUser(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userService.deleteUser(id);
    }
}
