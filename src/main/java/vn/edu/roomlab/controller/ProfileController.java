package vn.edu.roomlab.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.edu.roomlab.service.UserService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/update")
    public String updateProfile(
            @RequestParam String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateOwnProfile(principal.getName(), fullName, email, phone);
            redirectAttributes.addFlashAttribute("profileUpdated", true);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("profileError", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            userService.changeOwnPassword(
                    principal.getName(), currentPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("passwordChanged", true);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("profileError", e.getMessage());
        }
        return "redirect:/profile";
    }
}
