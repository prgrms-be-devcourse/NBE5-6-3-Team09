package com.codemap.core.admin.controller;

import com.codemap.core.admin.dto.AdminUserUpdateDto;
import com.codemap.core.admin.service.AdminUserService;
import com.codemap.core.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminService;

    @GetMapping() // 회원 목록 조회 -> 기본 진입
    public String showUserList(Model model) {
        List<User> users = adminService.findAllUsers(); // 모든 회원 조회
        model.addAttribute("users", users);
        return "admin/user/manage-users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = adminService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다. id=" + id));
        model.addAttribute("user", user);
        return "admin/user/edit-users";
    }

    @PatchMapping("/{id}")
    public String updateUser(@PathVariable Long id,
        @ModelAttribute AdminUserUpdateDto dto,
        RedirectAttributes redirectAttributes) {
        try {
            // currentPassword 검증 없이 새 비밀번호 바로 반영
            adminService.updateUserByAdmin(id, dto); // 메서드 이름도 명확히 분리 추천
            redirectAttributes.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }


    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.deleteUser(id);
        redirectAttributes.addFlashAttribute("message", "회원이 삭제되었습니다.");
        return "redirect:/admin/users";
    }
}