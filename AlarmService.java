package com.alarmclock.service;

import com.alarmclock.model.Alarm;
import java.util.List;
import java.util.Optional;

public interface AlarmService {
    List<Alarm> getAllAlarms();
    Optional<Alarm> getAlarmById(Long id);
    Alarm saveAlarm(Alarm alarm);
    Optional<Alarm> updateAlarm(Long id, Alarm alarm);
    void deleteAlarm(Long id);
} 