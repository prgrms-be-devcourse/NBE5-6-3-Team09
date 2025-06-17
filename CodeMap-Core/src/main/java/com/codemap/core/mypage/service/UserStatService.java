package com.codemap.core.mypage.service;

import com.codemap.core.infra.error.exceptions.CommonException;
import com.codemap.core.infra.response.ResponseCode;
import com.codemap.core.mypage.dto.UserStatDto;
import com.codemap.core.mypage.dto.UserUpdateRequestDto;
import com.codemap.core.mypage.repository.UserStatRepository;
import com.codemap.core.user.domain.User;
import com.codemap.core.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserStatService {

    private final UserStatRepository userStatRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserStatDto getStatForUser(Long userId) {
        ObjectMapper objectMapper = new ObjectMapper();

        return userStatRepository.findByUserId(userId)
            .map(stat -> {
                String json = stat.getDailyCompletionRate();
                Map<String, Integer> parsedMap;

                try {
                    parsedMap = objectMapper.readValue(json, new TypeReference<>() {});
                } catch (Exception e) {
                    parsedMap = new LinkedHashMap<>();
                }

                List<String> chartDates = new ArrayList<>(parsedMap.keySet());
                List<Integer> chartFocusMinutes = new ArrayList<>(parsedMap.values());

                return UserStatDto.builder()
                    .totalFocusMinutes(stat.getTotalFocusMinutes())
                    .dailyCompletionRateJson(json) // ← JS에서 직접 쓰고 싶으면 유지
                    .chartDates(chartDates)
                    .chartFocusMinutes(chartFocusMinutes)
                    .build();
            })
            .orElse(UserStatDto.builder()
                .totalFocusMinutes(0)
                .dailyCompletionRateJson("{}")
                .chartDates(new ArrayList<>())
                .chartFocusMinutes(new ArrayList<>())
                .build());
    }

    @Transactional
    public void updateUserInfo(Long userId, UserUpdateRequestDto dto) {
        // 추후 구현
    }

    @Transactional
    public void updateNotificationSetting(Long userId, Boolean enabled) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST));
        user.setNotificationEnabled(enabled);
    }
}