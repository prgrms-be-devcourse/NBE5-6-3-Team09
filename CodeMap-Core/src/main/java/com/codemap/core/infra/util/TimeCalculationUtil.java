package com.codemap.core.infra.util;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class TimeCalculationUtil {


    // 시작 시간과 종료 시간을 받아서 분 단위로 계산
    // 1분 미만이면 0, 1분 이상이면 올림 처리

    public static Integer calculateDurationInMinutes(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return 0;
        }

        long seconds = Duration.between(startTime, endTime).getSeconds();
        return seconds < 60 ? 0 : (int) Math.ceil(seconds / 60.0);
    }


    //초를 분으로 변환 (1분 미만은 0)

    public static Integer secondsToMinutes(Long seconds) {
        if (seconds == null || seconds < 60) {
            return 0;
        }
        return (int) Math.ceil(seconds / 60.0);
    }
}