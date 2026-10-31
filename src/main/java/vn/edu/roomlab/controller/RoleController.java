
package vn.edu.roomlab.controller;

import org.springframework.web.bind.annotation.*;
import vn.edu.roomlab.entity.Role;
import vn.edu.roomlab.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // Lấy tất cả roles
    @GetMapping
    public List<Role> getAll() {
        return roleService.getAllRoles();
    }

    // Lấy role theo ID
    @GetMapping("/{id}")
    public Role getById(@PathVariable Integer id) {
        return roleService.getRoleById(id);
    }

    // Thêm role
    @PostMapping
    public Role create(@RequestBody Role role) {
        return roleService.saveRole(role);
    }

    // Cập nhật role
    @PutMapping("/{id}")
    public Role update(@PathVariable Integer id,
                       @RequestBody Role role) {
        role.setRoleId(id);
        return roleService.saveRole(role);
    }

    // Xóa role
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        roleService.deleteRole(id);
    }
}
