package com.codemap.core.admin.service;

import com.codemap.core.admin.dto.AdminUserUpdateDto;
import com.codemap.core.routine.repository.CodingTestReviewRepository;
import com.codemap.core.routine.repository.DailyRoutineRepository;
import com.codemap.core.routine.repository.PomodoroSessionRepository;
import com.codemap.core.todo.repository.TodoRepository;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DailyRoutineRepository dailyRoutineRepository;
    private final TodoRepository todoRepository;
    private final PomodoroSessionRepository pomodoroSessionRepository;
    private final CodingTestReviewRepository codingTestReviewRepository;

    public List<User> findAllUsers() {
        return userRepository.findAllByRole("ROLE_USER");
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void updateUserByAdmin(Long id, AdminUserUpdateDto dto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다. id=" + id));

        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());

        // ⚠️ 입력값이 비어있지 않을 때만 업데이트
        if (dto.getNickname() != null && !dto.getNickname().isBlank()) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            String encoded = passwordEncoder.encode(dto.getNewPassword());
            user.setPassword(encoded);
        }

        userRepository.save(user); // 변경 감지 방식이라면 생략 가능
    }


    public void deleteUser(Long id) {
        // 1. 연관 데이터 먼저 삭제
        codingTestReviewRepository.deleteByUserId(id); // 코딩테스트 회고 삭제
        pomodoroSessionRepository.deleteByUserId(id);
        dailyRoutineRepository.deleteByUserId(id);
        todoRepository.deleteByUserId(id);

        // 2. 유저 삭제
        userRepository.deleteById(id);
    }
}