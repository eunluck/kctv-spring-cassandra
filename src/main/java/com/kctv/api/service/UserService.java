package com.kctv.api.service;


import com.kctv.api.advice.exception.CNotFoundEmailException;
import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;


    public UserInfo findByUserId(UUID uuid){
        return userRepository.findByUserId(uuid);
    }

    public Optional<UserInfo> checkByEmail(String email, String emailType){
        return userRepository.findByUserEmailAndUserEmailType(email,emailType);
    }


    public Optional<UserInfo> userLoginService(String email, String emailType, String pwd){
        return userRepository.findByUserEmailAndUserEmailTypeAndUserPassword(email,emailType,pwd);

    }
    public Optional<UserInfo> userSnsLoginService(String snsKey){
        return userRepository.findByUserSnsKey(snsKey);

    }

    public UserInfo userSignUpService(UserInfo userInfo){
        userInfo.setRoles(Collections.singletonList("ROLE_USER"));
        userInfo.setUserId(UUID.randomUUID());
        userInfo.setUserStatus("NORMAL");
        userInfo.setCreateDate(new Date());
        return userRepository.save(userInfo);

    }

    public List<UserInfo> getAllUserService(){
        return userRepository.findAll();
    }

    public UserInfo userUpdateService(UserInfo userInfo) {

       UserInfo userBefore = findByUserId(userInfo.getUserId());
       userBefore.setAccept(userInfo.getAccept());
       userBefore.setUpdateDate(new Date());
       userBefore.setUserMac(userInfo.getUserMac());
       userBefore.setUserNickname(userInfo.getUserNickname());
       userBefore.setUserPassword(userInfo.getUserPassword());
       userBefore.setUserPhone(userInfo.getUserPhone());

        return userRepository.save(userBefore);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws CUserNotFoundException {

        return userRepository.findByUserId(UUID.fromString(s));
    }




}
