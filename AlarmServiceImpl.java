package com.alarmclock.service;

import com.alarmclock.model.Alarm;
import com.alarmclock.repository.AlarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    private AlarmRepository alarmRepository;

    @Override
    public List<Alarm> getAllAlarms() {
        return alarmRepository.findAll();
    }

    @Override
    public Optional<Alarm> getAlarmById(Long id) {
        return alarmRepository.findById(id);
    }

    @Override
    public Alarm saveAlarm(Alarm alarm) {
        return alarmRepository.save(alarm);
    }

    @Override
    public Optional<Alarm> updateAlarm(Long id, Alarm alarmDetails) {
        return alarmRepository.findById(id)
                .map(existingAlarm -> {
                    existingAlarm.setTime(alarmDetails.getTime());
                    existingAlarm.setMessage(alarmDetails.getMessage());
                    existingAlarm.setSound(alarmDetails.getSound());
                    existingAlarm.setRepeat(alarmDetails.isRepeat());
                    existingAlarm.setSnooze(alarmDetails.getSnooze());
                    existingAlarm.setActive(alarmDetails.isActive());
                    return alarmRepository.save(existingAlarm);
                });
    }

    @Override
    public void deleteAlarm(Long id) {
        alarmRepository.deleteById(id);
    }
} 