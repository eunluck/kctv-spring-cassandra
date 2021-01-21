package com.kctv.api.service;


import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.kctv.api.advice.exception.CUserExistException;
import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.entity.user.InviteFriends;
import com.kctv.api.entity.user.UserInterestTag;
import com.kctv.api.repository.user.InviteRepository;
import com.kctv.api.repository.user.UserInterestTagRepository;
import com.kctv.api.repository.user.UserLikeRepository;
import com.kctv.api.util.RedisUtil;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {


    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserInterestTagRepository userInterestTagRepository;
    private final UserLikeRepository userLikeRepository;
    private final String EMAIL_LINK;
    //private final String EMAIL_LINK = "http://192.168.0.56:8081/v1/verify/";
    private final String EMAIL_SUB = "KCTV 회원가입 인증 메일입니다.";


    public UserService(@Lazy PasswordEncoder passwordEncoder, RedisUtil redisUtil, UserRepository userRepository, EmailService emailService, UserInterestTagRepository userInterestTagRepository, UserLikeRepository userLikeRepository, @Value("${costom.host.path}") String email_link) {
        this.passwordEncoder = passwordEncoder;
        this.redisUtil = redisUtil;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userInterestTagRepository = userInterestTagRepository;
        this.userLikeRepository = userLikeRepository;
        this.EMAIL_LINK = email_link;

    }

    public UserInfo findByUserId(UUID uuid){
        return userRepository.findByUserId(uuid).orElseThrow(CUserNotFoundException::new);
    }

    public Optional<UserInfo> checkByEmail(String email, String emailType){
        return userRepository.findByUserEmailAndUserEmailType(email,emailType);
    }


    public Optional<UserInfo> userLoginService(String email, String emailType, String pwd){
      UserInfo loginUser = userRepository.findByUserEmailAndUserEmailType(email,emailType).orElseThrow(CUserNotFoundException::new);

      if (passwordEncoder.matches(pwd,loginUser.getUserPassword()))
        return Optional.ofNullable(loginUser);
      else
        return Optional.empty();

    }
    public Optional<UserInfo> userSnsLoginService(String snsKey){
        return userRepository.findByUserSnsKey(snsKey);

    }

    public UserInfo userSignUpService(UserInfo userInfo){

        String code = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거해 주었다.
        code = code.substring(0, 6);


        userInfo.setUserId(UUID.randomUUID());
        if ("user".equals(userInfo.getUserEmailType())) {
            userInfo.setRoles(Collections.singletonList("ROLE_NOT_VERIFY_EMAIL"));
            userInfo.setUserPassword(passwordEncoder.encode(userInfo.getUserPassword()));
        }else{
            userInfo.setRoles(Collections.singletonList("ROLE_USER"));
        }

        userInfo.setUserStatus("NORMAL");
        userInfo.setCreateDate(new Date());
        userInfo.setInviteCode(code);

        UserInfo result = Optional.ofNullable(userRepository.insert(userInfo)).orElseThrow(CUserExistException::new);


        if ("user".equals(result.getUserEmailType()))


            sendVerificationMail(result);


        return result;

    }



    public void sendVerificationMail(UserInfo userInfo){

        redisUtil.setDataExpire(String.valueOf(userInfo.getUserId()),String.valueOf(userInfo.getUserId()),1000L * 60 * 60 * 24 ); // 코드는 24시간  유지됌
        emailService.sendMail(userInfo.getUserEmail(),EMAIL_SUB,EMAIL_LINK+String.valueOf(userInfo.getUserId()));


    }

    public void sendTempPassword(String email,String emailType){


        UserInfo findUser = userRepository.findByUserEmailAndUserEmailType(email,emailType).orElseThrow(CUserNotFoundException::new);

        String uuid = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거해 주었다.
        uuid = uuid.substring(0, 8);

        HashCode md5 = Hashing.md5().hashString(uuid, Charsets.UTF_8);

        System.out.println(md5 +" :: 해시된값");
        findUser.setUserPassword(passwordEncoder.encode(md5.toString()));

        List<String> role = findUser.getRoles();
        role.add("ROLE_TEMP_PASSWORD");
        findUser.setRoles(role);


        Optional.ofNullable(userRepository.save(findUser)).orElseThrow(CUserNotFoundException::new);

        emailService.sendMail(findUser.getUserEmail(),"임시비밀번호를 발급해드립니다.","임시비밀번호:"+uuid);

        //redisUtil.setDataExpire(uuid,uuid,1000L * 60 * 60 * 24 ); // 코드는 24시간  유지됌


    }


    public void verifyEmail(String key) throws CUserNotFoundException {

        UUID userId = UUID.fromString(redisUtil.getData(key));
        UserInfo user = userRepository.findByUserId(userId).orElseThrow(CUserNotFoundException::new);

        user.setRoles(Collections.singletonList("ROLE_USER"));

        userRepository.save(user);
        redisUtil.deleteData(key);

    }

    public UserInfo modifyUserRole(UserInfo userInfo, String role){

        List<String> userRole = userInfo.getRoles();
        userRole.add(role);
        userInfo.setRoles(userRole);
        return userRepository.save(userInfo);
    }



    public List<UserInfo> getAllUserService(){
        return userRepository.findAll();
    }


    public UserInfo userUpdateService(UserInfo userInfo) {

       UserInfo userBefore = findByUserId(userInfo.getUserId());
       if(userInfo.getAccept() != null && !userInfo.getAccept().isEmpty())
        userBefore.setAccept(userInfo.getAccept());

       userBefore.setUpdateDate(new Date());
       if(userInfo.getUserMac() != null && !userInfo.getUserMac().isEmpty())
        userBefore.setUserMac(userInfo.getUserMac());
       if(userInfo.getUserNickname() != null && !userInfo.getUserNickname().trim().isEmpty())
        userBefore.setUserNickname(userInfo.getUserNickname());
       if(userInfo.getUserPassword() != null && !userInfo.getUserPassword().trim().isEmpty()){
        userBefore.setUserPassword(passwordEncoder.encode(userInfo.getUserPassword()));
        List<String> role = userBefore.getRoles().stream().filter(s -> !s.equals("ROLE_TEMP_PASSWORD")).collect(Collectors.toList());
        userBefore.setRoles(role);
       }
       if(userInfo.getUserPhone() != null && !userInfo.getUserPhone().trim().isEmpty())
        userBefore.setUserPhone(userInfo.getUserPhone());

       if(userInfo.getUserAddress() != null && !userInfo.getUserAddress().trim().isEmpty())
        userBefore.setUserAddress(userInfo.getUserAddress());
       if(userInfo.getUserGender() != null && !userInfo.getUserGender().trim().isEmpty())
        userBefore.setUserGender(userInfo.getUserGender());
       if(userInfo.getUserBirth() != null && !userInfo.getUserBirth().trim().isEmpty())
        userBefore.setUserBirth(userInfo.getUserBirth());

        return userRepository.save(userBefore);
    }

    public UserInterestTag userInterestTagService(UserInterestTag userTags){

        return Optional.ofNullable(userInterestTagRepository.save(userTags)).orElseThrow(CUserNotFoundException::new);
    }

    public Optional<UserInterestTag> getUserInterestTag(UUID uuid){

        return userInterestTagRepository.findByUserId(uuid);

    }


    @Override
    public UserDetails loadUserByUsername(String s) throws CUserNotFoundException {

        return userRepository.findByUserId(UUID.fromString(s)).orElseThrow(CUserNotFoundException::new);
    }



}
