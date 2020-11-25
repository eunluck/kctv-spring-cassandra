package com.kctv.api.service;


import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.advice.exception.CUserExistException;
import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.entity.user.UserInterestTag;
import com.kctv.api.repository.user.UserInterestTagRepository;
import com.kctv.api.util.RedisUtil;
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

    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserInterestTagRepository userInterestTagRepository;
    private final String EMAIL_LINK = "http://localhost:8081/v1/verify/";
    //private final String EMAIL_LINK = "http://192.168.0.56:8081/v1/verify/";
    private final String EMAIL_SUB = "KCTV 회원가입 인증 메일입니다.";




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

        userInfo.setUserId(UUID.randomUUID());
        userInfo.setRoles(Collections.singletonList("ROLE_NOT_VERIFY_EMAIL"));
        userInfo.setUserStatus("NORMAL");
        userInfo.setCreateDate(new Date());

        UserInfo result = Optional.ofNullable(userRepository.insert(userInfo)).orElseThrow(CUserExistException::new);


        if (result.getUserEmailType().equals("user"))

            sendVerificationMail(result);


        return result;

    }



    public void sendVerificationMail(UserInfo userInfo){

        redisUtil.setDataExpire(String.valueOf(userInfo.getUserId()),String.valueOf(userInfo.getUserId()),60*30L); // 코드는 3분동안 유지됌
        emailService.sendMail(userInfo.getUserEmail(),EMAIL_SUB,EMAIL_LINK+String.valueOf(userInfo.getUserId()));


    }


    public void verifyEmail(String key) throws CUserNotFoundException {

        UUID userId = UUID.fromString(redisUtil.getData(key));
        UserInfo user = Optional.ofNullable(userRepository.findByUserId(userId)).orElseThrow(CUserNotFoundException::new);

        modifyUserRole(user,"ROLE_USER");
        redisUtil.deleteData(key);

    }

    public UserInfo modifyUserRole(UserInfo userInfo, String role){

        userInfo.setRoles(Collections.singletonList(role));
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

    public UserInterestTag userInterestTagService(UserInterestTag userTags){

        return Optional.ofNullable(userInterestTagRepository.save(userTags)).orElseThrow(CUserNotFoundException::new);
    }

    public UserInterestTag getUserInterestTag(UUID uuid){

        UserInterestTag userTag = userInterestTagRepository.findByUserId(uuid).orElseThrow(CResourceNotExistException::new);

        return userTag;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws CUserNotFoundException {

        return userRepository.findByUserId(UUID.fromString(s));
    }




}
