package com.codemap.core.routine.repository;

import com.codemap.core.routine.domain.DailyRoutine;
import com.codemap.core.user.domain.User;
import java.time.LocalDate;
import java.util.List;

public interface DailyRoutineRepositoryCustom {
    List<DailyRoutine> findActiveRoutinesByUser(User user);
    List<DailyRoutine> findCompletedRoutinesByUser(User user);
    List<DailyRoutine> findPassedRoutinesByUser(User user);
    List<DailyRoutine> findRoutinesByUserAndDate(User user, LocalDate date);
}