package com.kctv.api.service;

import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.entity.user.InviteFriends;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.repository.user.InviteRepository;
import com.kctv.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final UserRepository userRepository;
    private final InviteRepository inviteRepository;

    public Optional<UserInfo> findUserByCode(String code){

        return userRepository.findByInviteCode(code);
    }
    
    public boolean inviteDoubleCheck(UUID userId, UUID friendId){

        return inviteRepository.findByUserIdAndFriendId(userId,friendId).isPresent();
    }


    public boolean saveInviteCode(InviteFriends inviteFriends){


        Optional.of(inviteRepository.save(inviteFriends)).orElseThrow(CResourceNotExistException::new);

        return true;

    }

    public void deleteInviteCode(InviteFriends inviteFriends){

        inviteRepository.delete(inviteFriends);

    }

}
