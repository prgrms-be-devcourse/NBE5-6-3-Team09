package com.codemap.core.user.controller;

import com.codemap.core.user.domain.User;
import com.codemap.core.user.dto.NicknameUpdateDto;
import com.codemap.core.user.dto.PasswordUpdateDto;
import com.codemap.core.user.service.UserService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingController {

    private final UserService userService;

    @PostMapping("/nickname")
    public String updateNickname(
        Principal principal,
        @ModelAttribute NicknameUpdateDto dto,
        RedirectAttributes redirectAttributes) {

        String email = principal.getName();
        User user = userService.findByEmail(email);
        userService.updateNickname(user.getId(), dto.getNickname());

        redirectAttributes.addFlashAttribute("message", "닉네임이 변경되었습니다.");
        return "redirect:/settings";
    }

    @PostMapping("/password")
    public String updatePassword(
        Principal principal,
        @ModelAttribute PasswordUpdateDto dto,
        RedirectAttributes redirectAttributes) {

        String email = principal.getName();
        User user = userService.findByEmail(email);
        userService.updatePassword(user.getId(), dto.getCurrentPassword(), dto.getNewPassword());

        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
        return "redirect:/settings";
    }

    @PostMapping("/notification")
    public String updateNotification(@RequestParam("notificationEnabled") List<String> values,
        Principal principal,
        RedirectAttributes redirectAttributes) {
        boolean enabled = values.contains("true");

        String email = principal.getName();
        User user = userService.findByEmail(email);
        userService.updateNotificationSetting(user.getId(), enabled);

        redirectAttributes.addFlashAttribute("message", "알림 설정이 변경되었습니다.");
        return "redirect:/settings";
    }
}