package com.kctv.api.service;


import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.entity.log.AppClkLog;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.repository.user.UserLoggingRepository;
import com.kctv.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LoggingService {

    private final UserLoggingRepository loggingRepository;


    public List<AppClkLog> findByUserId(UUID uuid){
        return loggingRepository.findByUserId(uuid);
    }

    public AppClkLog saveClkLog(AppClkLog appClkLog){
        SimpleDateFormat date = new SimpleDateFormat("yyyyMM");
        appClkLog.setMonth(Integer.valueOf(date.format(new Date())));
        appClkLog.setBatchDt(new Date());
        return loggingRepository.save(appClkLog);
    }



}
