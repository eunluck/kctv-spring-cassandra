
package com.kctv.api.service;


import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.entity.payment.PaymentCode;
import com.kctv.api.entity.payment.PaymentInfo;
import com.kctv.api.entity.place.PlaceInfo;
import com.kctv.api.entity.stylecard.StyleCardInfo;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.entity.user.UserLikePartner;
import com.kctv.api.entity.user.UserScrapCard;
import com.kctv.api.model.ap.WakeupPermission;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.ap.WakeUpPermissionRepository;
import com.kctv.api.repository.card.StyleCardCounterDayRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import com.kctv.api.repository.payment.PaymentCodeRepository;
import com.kctv.api.repository.payment.UserPaymentRepository;
import com.kctv.api.repository.user.UserLikeRepository;
import com.kctv.api.repository.user.UserRepository;
import com.kctv.api.repository.user.UserScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentCodeRepository paymentCodeRepository;
    private final UserPaymentRepository paymentRepository;
    private final WakeUpPermissionRepository wakeUpPermissionRepository;
    private final UserRepository userRepository;

    public PaymentInfo subscribe(UUID userId, String appPaymentCode){

        PaymentCode code = Optional.of(paymentCodeRepository.findByAppPaymentCode(appPaymentCode)).orElseThrow(() -> new CResourceNotExistException(appPaymentCode));
        UserInfo requestUser = userRepository.findByUserId(userId).orElseThrow(CUserNotFoundException::new);

        WakeupPermission permissionUser = wakeUpPermissionRepository.findByUserId(requestUser.getUserId()).orElseThrow(() -> new CUserNotFoundException(userId.toString()));

        permissionUser.subscribeUser(code.getPeriod(),code.getAppPaymentCode(),code.getVolumeLimit());

        Optional.of(wakeUpPermissionRepository.insert(permissionUser)).orElseThrow(CResourceNotExistException::new); //결제실패 에러작성

        return paymentRepository.insert(new PaymentInfo(requestUser.getUserId(),code.getAppPaymentCode(),new Date()));
    }



}

