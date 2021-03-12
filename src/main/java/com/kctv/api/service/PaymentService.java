
package com.kctv.api.service;


import com.kctv.api.advice.exception.CNotFoundCodeException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.model.payment.PaymentCodeEntity;
import com.kctv.api.model.payment.PaymentInfoEntity;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.model.ap.WakeupPermissionEntity;
import com.kctv.api.repository.ap.WakeUpPermissionRepository;
import com.kctv.api.repository.payment.PaymentCodeRepository;
import com.kctv.api.repository.payment.UserPaymentRepository;
import com.kctv.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentCodeRepository paymentCodeRepository;
    private final UserPaymentRepository paymentRepository;
    private final WakeUpPermissionRepository wakeUpPermissionRepository;
    private final UserRepository userRepository;


    public PaymentInfoEntity subscribe(UUID userId, String appPaymentCode){

        PaymentCodeEntity code = Optional.ofNullable(paymentCodeRepository.findByAppPaymentCode(appPaymentCode)).orElseThrow(() -> new CNotFoundCodeException(appPaymentCode));
        UserInfoEntity requestUser = userRepository.findByUserId(userId).orElseThrow(CUserNotFoundException::new);

        WakeupPermissionEntity permissionUser = wakeUpPermissionRepository.findByUserId(requestUser.getUserId()).orElseThrow(() -> new CUserNotFoundException(userId.toString()));

        permissionUser.subscribeUser(code.getPeriod(),code.getAppPaymentCode(),code.getVolumeLimit());

        Optional.of(wakeUpPermissionRepository.insert(permissionUser)).orElseThrow(CResourceNotExistException::new); //결제실패 에러작성

        Date userExpire = new Date(permissionUser.getExpireEpoch());

        return !appPaymentCode.equals("FREE") && !appPaymentCode.equals("p001") ? paymentRepository.insert(new PaymentInfoEntity(permissionUser.getUserId(),code.getAppPaymentCode(),new Date(),userExpire)) : new PaymentInfoEntity(userId,"FREE",new Date());

    }

    public List<PaymentInfoEntity> findByUserId(UUID userId){

        return paymentRepository.findByUserId(userId);
    }

    public List<PaymentCodeEntity> findByCodeList(){

        return paymentCodeRepository.findAll();
    }


    public PaymentCodeEntity findByAppPaymentCode(String code){

        return paymentCodeRepository.findByAppPaymentCode(code);
    }

}

