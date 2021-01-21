package com.kctv.api.service;

import com.kctv.api.entity.user.InviteFriends;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.repository.user.InviteRepository;
import com.kctv.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final UserRepository userRepository;
    private final InviteRepository inviteRepository;

    public Optional<UserInfo> findUserByCode(String code){

        return userRepository.findByInviteCode(code);
    }

    public boolean saveInviteCode(InviteFriends inviteFriends){


        return Optional.ofNullable(inviteRepository.save(inviteFriends)).isPresent();

    }

}
