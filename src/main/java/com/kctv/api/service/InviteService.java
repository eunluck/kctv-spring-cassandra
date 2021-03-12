package com.kctv.api.service;

import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.model.payment.PaymentCodeEntity;
import com.kctv.api.model.payment.PaymentInfoEntity;
import com.kctv.api.model.user.InviteFriendsEntity;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.repository.payment.PaymentCodeRepository;
import com.kctv.api.repository.payment.UserPaymentRepository;
import com.kctv.api.repository.user.InviteRepository;
import com.kctv.api.repository.user.UserInfoByEmailRepository;
import com.kctv.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final UserRepository userRepository;
    private final InviteRepository inviteRepository;
    private final UserPaymentRepository userPaymentRepository;
    private final PaymentCodeRepository paymentCodeRepository;
    private final UserInfoByEmailRepository userInfoByEmailRepository;

    public Optional<UserInfoEntity> findUserByCode(String code){

        return userRepository.findByInviteCode(code);
    }
    
    public boolean inviteDoubleCheck(UUID userId, UUID friendId){

        return inviteRepository.findByUserIdAndFriendId(userId,friendId).isPresent();
    }


    public boolean saveInviteCode(InviteFriendsEntity inviteFriendsEntity,UserInfoEntity userInfoEntity){

        inviteRepository.save(inviteFriendsEntity);
        userPaymentRepository.insert(new PaymentInfoEntity("f001",inviteFriendsEntity.getFriendId(),userInfoEntity.getUserEmail(),inviteFriendsEntity.getCreateDt()));

        return true;
    }

    public void deleteInviteCode(InviteFriendsEntity inviteFriendsEntity){

        inviteRepository.delete(inviteFriendsEntity);

    }

}
