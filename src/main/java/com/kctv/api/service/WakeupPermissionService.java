package com.kctv.api.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.entity.admin.QnaAnswer;
import com.kctv.api.entity.qna.QnaByUserEntity;
import com.kctv.api.model.ap.FindApRequest;
import com.kctv.api.model.ap.WakeupPermission;
import com.kctv.api.model.qna.QnaDto;
import com.kctv.api.model.qna.QnaRequest;
import com.kctv.api.repository.ap.WakeUpPermissionRepository;
import com.kctv.api.repository.qna.QnaAnswerRepository;
import com.kctv.api.repository.qna.QnaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@AllArgsConstructor
@Service
public class WakeupPermissionService {

    private final WakeUpPermissionRepository wakeUpPermissionRepository;



    public WakeupPermission saveUserIdToWakeUfPermission(FindApRequest apRequest) {

        return wakeUpPermissionRepository.save(new WakeupPermission(apRequest,new Date()));

    }

    public Optional<WakeupPermission> findPermissionByUserId(UUID uuid){

        return wakeUpPermissionRepository.findByUserId(uuid);

    }



}
