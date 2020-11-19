package com.kctv.api.service;


import com.kctv.api.advice.exception.CUserExistException;
import com.kctv.api.advice.exception.CUserNotFoundException;
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
    private final String EMAIL_LINK = "http://localhost:8081/v1/user/verify";

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

        System.out.println("userInfo service::"+userInfo.toString());
        userInfo.setRoles(Collections.singletonList("ROLE_NOT_VERIFY_EMAIL"));
        userInfo.setUserId(UUID.randomUUID());
        userInfo.setUserStatus("NORMAL");
        userInfo.setCreateDate(new Date());

        UserInfo result = Optional.ofNullable(userRepository.insert(userInfo)).orElseThrow(CUserExistException::new);


        if (result.getUserEmailType().equals("user"))
        System.out.println("userInfo service2::"+userInfo.toString());
            sendVerificationMail(result);
        System.out.println("userInfo service3::"+userInfo.toString());

        return result;

    }


    public void sendVerificationMail(UserInfo userInfo){


        redisUtil.setDataExpire(String.valueOf(userInfo.getUserId()),userInfo.getUserNickname(),60*30L); // 코드는 3분동안 유지됌
        emailService.sendMail(userInfo.getUserEmail(),"kctv 회원가입 인증 메일입니다.",EMAIL_LINK+String.valueOf(userInfo.getUserId()));

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

    @Override
    public UserDetails loadUserByUsername(String s) throws CUserNotFoundException {

        return userRepository.findByUserId(UUID.fromString(s));
    }




}
