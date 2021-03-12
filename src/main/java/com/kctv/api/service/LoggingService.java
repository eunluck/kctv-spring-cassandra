package com.kctv.api.service;


import com.kctv.api.model.log.AppClkLogEntitiy;
import com.kctv.api.repository.user.UserLoggingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LoggingService {

    private final UserLoggingRepository loggingRepository;


    public List<AppClkLogEntitiy> findByUserId(UUID uuid){
        return loggingRepository.findByUserId(uuid);
    }

    public AppClkLogEntitiy saveClkLog(AppClkLogEntitiy appClkLogEntitiy){
        SimpleDateFormat date = new SimpleDateFormat("yyyyMM");
        appClkLogEntitiy.setMonth(Integer.valueOf(date.format(new Date())));
        appClkLogEntitiy.setBatchDt(new Date());
        return loggingRepository.save(appClkLogEntitiy);
    }



}
