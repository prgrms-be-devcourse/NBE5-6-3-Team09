package com.codemap.core.user.service;

import com.codemap.core.infra.auth.Role;
import com.codemap.core.infra.error.exceptions.CommonException;
import com.codemap.core.infra.event.EventPublisher;
import com.codemap.core.infra.response.ResponseCode;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.dto.UserDto;
import com.codemap.core.user.form.SigninForm;
import com.codemap.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;
    private final EventPublisher eventPublisher;

    @Transactional
    public void signup(UserDto dto, Role role) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        // dto → entity 변환
        User user = mapper.map(dto, User.class);

        // 비밀번호 인코딩
        user.encodePassword(passwordEncoder.encode(dto.getPassword()));
        user.assignRole(role.name());

        User savedUser = userRepository.save(user);

        try {
            eventPublisher.publishSignupCompleteEvent(savedUser.getEmail());
            log.info("✅ 회원가입 완료 - Email: {}, 메일 이벤트 발행됨", savedUser.getEmail());
        } catch (Exception e) {
            log.error("❌ 메일 이벤트 발행 실패 - Email: {}", savedUser.getEmail(), e);
            // 메일 발송 실패해도 회원가입은 성공으로 처리
        }
    }

    public boolean isDuplicatedEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST));


    }
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST));
    }

    @Transactional(readOnly = true)
    public User login(SigninForm form) {
        User user = userRepository.findByEmail(form.getEmail())
            .orElseThrow(() -> new CommonException(ResponseCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            throw new CommonException(ResponseCode.BAD_REQUEST); // 혹은 custom 에러
        }

        return user;
    }

    public void updateNickname(Long userId, String newNickname) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        user.updateNickname(newNickname);
        userRepository.save(user);
    }

    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void updateNotificationSetting(Long userId, Boolean enabled) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        user.setNotificationEnabled(enabled);
        userRepository.save(user);
    }
}