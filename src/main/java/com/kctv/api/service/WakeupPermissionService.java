package com.kctv.api.service;

import com.kctv.api.model.ap.FindApRequest;
import com.kctv.api.model.ap.WakeupPermissionEntity;
import com.kctv.api.repository.ap.WakeUpPermissionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;


@AllArgsConstructor
@Service
public class WakeupPermissionService {

    private final WakeUpPermissionRepository wakeUpPermissionRepository;



    public WakeupPermissionEntity saveUserIdToWakeUfPermission(FindApRequest apRequest) {

        return wakeUpPermissionRepository.save(new WakeupPermissionEntity(apRequest,new Date()));

    }

    public WakeupPermissionEntity findPermissionByUserId(UUID uuid){

        WakeupPermissionEntity userPermission = wakeUpPermissionRepository.findByUserId(uuid).orElseGet(WakeupPermissionEntity::new);

        if(userPermission.getExpireEpoch() != null && userPermission.getExpireEpoch() != 0L){
            if (userPermission.getExpireEpoch() < System.currentTimeMillis()){
                userPermission.expirationUser();
                return wakeUpPermissionRepository.save(userPermission);
            }

        }


        return userPermission;

    }



}
