package com.codemap.core.routine.repository;

import com.codemap.core.routine.domain.DailyRoutine;
import com.codemap.core.routine.domain.QDailyRoutine;
import com.codemap.core.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DailyRoutineRepositoryCustomImpl implements DailyRoutineRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QDailyRoutine qDailyRoutine = QDailyRoutine.dailyRoutine;

    @Override
    public List<DailyRoutine> findActiveRoutinesByUser(User user) {
        return queryFactory
            .selectFrom(qDailyRoutine)
            .where(qDailyRoutine.user.eq(user)
                .and(qDailyRoutine.isDeleted.eq(false))
                .and(qDailyRoutine.status.eq("ACTIVE")))
            .orderBy(qDailyRoutine.createdAt.desc())
            .fetch();
    }

    @Override
    public List<DailyRoutine> findCompletedRoutinesByUser(User user) {
        return queryFactory
            .selectFrom(qDailyRoutine)
            .where(qDailyRoutine.user.eq(user)
                .and(qDailyRoutine.isDeleted.eq(false))
                .and(qDailyRoutine.status.eq("COMPLETED")))
            .orderBy(qDailyRoutine.completedAt.desc())
            .fetch();
    }

    @Override
    public List<DailyRoutine> findPassedRoutinesByUser(User user) {
        return queryFactory
            .selectFrom(qDailyRoutine)
            .where(qDailyRoutine.user.eq(user)
                .and(qDailyRoutine.isDeleted.eq(false))
                .and(qDailyRoutine.status.eq("PASS")))
            .orderBy(qDailyRoutine.updatedAt.desc())
            .fetch();
    }

    @Override
    public List<DailyRoutine> findRoutinesByUserAndDate(User user, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return queryFactory
            .selectFrom(qDailyRoutine)
            .where(qDailyRoutine.user.eq(user)
                .and(qDailyRoutine.isDeleted.eq(false))
                .and(qDailyRoutine.createdAt.between(startOfDay, endOfDay)))
            .orderBy(qDailyRoutine.createdAt.desc())
            .fetch();
    }
}