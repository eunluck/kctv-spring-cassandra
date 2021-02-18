package com.kctv.api.service;


import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.advice.exception.CUserExistException;
import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.entity.user.*;
import com.kctv.api.entity.visit.UserVisitHistoryEntity;
import com.kctv.api.model.ap.WakeupPermission;
import com.kctv.api.repository.ap.WakeUpPermissionRepository;
import com.kctv.api.repository.user.*;
import com.kctv.api.repository.visit.VisitHistoryRepository;
import com.kctv.api.util.AES256Util;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.krb5.internal.crypto.Aes256;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
public class UserService implements UserDetailsService {


    private final UserUniqueCodeRepository userUniqueCodeRepository;
    private final WakeUpPermissionRepository wakeUpPermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JavaMailSender emailSender;
    private final UserInterestTagRepository userInterestTagRepository;
    private final UserLikeRepository userLikeRepository;
    private final UserScrapRepository userScrapRepository;
    private final UserEmailVerifyRepository userEmailVerifyRepository;
    private final VisitHistoryRepository visitHistoryRepository;
    private final String EMAIL_LINK;
    //private final String EMAIL_LINK = "http://192.168.0.56:8081/v1/verify/";
    private final String EMAIL_SUB = "KCTV 회원가입 인증 메일입니다.";
    private final AES256Util aes = AES256Util.getInstance();


    public UserService(UserUniqueCodeRepository userUniqueCodeRepository, WakeUpPermissionRepository wakeUpPermissionRepository, @Lazy PasswordEncoder passwordEncoder, UserRepository userRepository, JavaMailSender emailSender, UserInterestTagRepository userInterestTagRepository, UserLikeRepository userLikeRepository, UserScrapRepository userScrapRepository, UserEmailVerifyRepository userEmailVerifyRepository, VisitHistoryRepository visitHistoryRepository, @Value("${costom.host.path}") String email_link) throws UnsupportedEncodingException {
        this.userUniqueCodeRepository = userUniqueCodeRepository;
        this.wakeUpPermissionRepository = wakeUpPermissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
        this.userInterestTagRepository = userInterestTagRepository;
        this.userLikeRepository = userLikeRepository;
        this.userScrapRepository = userScrapRepository;
        this.userEmailVerifyRepository = userEmailVerifyRepository;
        this.visitHistoryRepository = visitHistoryRepository;
        this.EMAIL_LINK = email_link;

    }

    public UserInfo deleteUserInfo(UserInfo userInfo){
        WakeupPermission userPermission;
        UserUniqueCode userUniqueCode;
        UserInterestTag userTags;
        List<UserLikePartner> userLikePartners;
        List<UserScrapCard> userScrapCards;
        UserEmailVerify userEmailVerify;
        List<UserVisitHistoryEntity> userVisitHistoryEntities;
        try {
            userPermission = wakeUpPermissionRepository.findByUserId(userInfo.getUserId()).orElseThrow(CUserNotFoundException::new);
            userUniqueCode = userUniqueCodeRepository.findByUniqueCode(userPermission.getUniqueCode()).orElseThrow(CUserNotFoundException::new);
            userTags = userInterestTagRepository.findByUserId(userInfo.getUserId()).orElseGet(() -> null);
            userLikePartners = userLikeRepository.findByUserId(userInfo.getUserId());
            userScrapCards = userScrapRepository.findByUserId(userInfo.getUserId());
            userEmailVerify = userEmailVerifyRepository.findByUserId(userInfo.getUserId()).orElseGet(() -> null);
            userVisitHistoryEntities = visitHistoryRepository.findByUserId(userInfo.getUserId());
        }catch (Exception e){
            e.printStackTrace();
            throw new CUserNotFoundException();
        }

        userRepository.delete(userInfo);
        wakeUpPermissionRepository.delete(userPermission);
        userUniqueCodeRepository.delete(userUniqueCode);
        if (userTags != null)
            userInterestTagRepository.delete(userTags);
        if (CollectionUtils.isNotEmpty(userLikePartners))
            userLikeRepository.deleteAll(userLikePartners);
        if (CollectionUtils.isNotEmpty(userScrapCards))
            userScrapRepository.deleteAll(userScrapCards);
        if(CollectionUtils.isNotEmpty(userVisitHistoryEntities))
            visitHistoryRepository.deleteAll(userVisitHistoryEntities);
        if (userEmailVerify != null)
            userEmailVerifyRepository.delete(userEmailVerify);

        return userInfo;
    }

    public UserInfo findByUserId(UUID uuid) {

        UserInfo user = userRepository.findByUserId(uuid).orElseThrow(CUserNotFoundException::new);
        user.decryptInfo();

        return user;
    }

    @SneakyThrows
    public Optional<UserInfo> checkByEmail(String email, String emailType) {
        email = aes.encrypt(email);

        return userRepository.findByUserEmailAndUserEmailType(email, emailType);
    }

    @SneakyThrows
    public Optional<UserInfo> userLoginService(String email, String emailType, String pwd) {
        UserInfo loginUser = userRepository.findByUserEmailAndUserEmailType(email, emailType).orElseThrow(CUserNotFoundException::new);


        return passwordEncoder.matches(pwd, loginUser.getUserPassword()) ? Optional.of(loginUser) : Optional.empty();
    }

    public Optional<UserInfo> userSnsLoginService(String snsKey) {
        return userRepository.findByUserSnsKey(snsKey);

    }

    public String createUniqueCode(UUID userId) {

        String resultCode;

        do {
            String code = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거해 주었다.
            resultCode = code.substring(0, 6);

        } while (userUniqueCodeRepository.findByUniqueCode(resultCode).isPresent());

        return userUniqueCodeRepository.insert(new UserUniqueCode(resultCode, userId, new Date())).getUniqueCode();

    }

    public UserInfo userSignUpService(UserInfo userInfo) throws GeneralSecurityException, UnsupportedEncodingException {



        String code = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거해 주었다.
        code = code.substring(0, 6);
        userInfo.setUserId(UUID.randomUUID());
        String userUniqueCode = createUniqueCode(userInfo.getUserId());

        WakeupPermission wakeupPermission = null;

        if ("user".equals(userInfo.getUserEmailType())) {
            userInfo.setRoles(Collections.singletonList("ROLE_NOT_VERIFY_EMAIL"));
            userInfo.setUserPassword(passwordEncoder.encode(userInfo.getUserPassword()));
        } else {
            userInfo.setRoles(Collections.singletonList("ROLE_USER"));

            wakeupPermission = wakeUpPermissionRepository.save(new WakeupPermission(userInfo, userUniqueCode));
        }

        userInfo.setUserStatus("NORMAL");
        userInfo.setCreateDate(new Date());
        userInfo.setInviteCode(code);

        UserInfo result = Optional.of(userRepository.insert(userInfo)).orElseThrow(CUserExistException::new);


        if ("user".equals(result.getUserEmailType())){
            //TODO 여기에 이메일인증로직 추가
            try {
                userEmailVerifySave(userUniqueCode,userInfo.getUserId(),userInfo.getUserEmail());

            }catch (Exception e){
                e.printStackTrace();
                userRepository.delete(result);
                wakeUpPermissionRepository.delete(wakeupPermission);
                throw new CUserNotFoundException();
            }
        }

        return result;

    }

    @SneakyThrows
    public boolean userEmailVerifySave(String uniqueCode, UUID userId, String email){




        userEmailVerifyRepository.save(new UserEmailVerify(uniqueCode,new Date(),email, userId));


        sendMailMessage(email, EMAIL_SUB, EMAIL_LINK + uniqueCode); //메일전송


        return true;

    }

    @SneakyThrows
    public void userVerifyEmailResend(UserInfo userInfo){

        userInfo.setUserEmail(aes.encrypt(userInfo.getUserEmail()));
         userEmailVerifyRepository.findByUserId(userInfo.getUserId())
                 .ifPresent(beforeUserEmailVerify -> userEmailVerifyRepository.delete(beforeUserEmailVerify));


        userEmailVerifySave(createUniqueCode(userInfo.getUserId()),userInfo.getUserId(),userInfo.getUserEmail());

    }


    @SneakyThrows
    public void sendMailMessage(String userEmail, String subject, String text) {

        System.out.println(userEmail);
        System.out.println(aes.decrypt(userEmail));
        SimpleMailMessage sender = new SimpleMailMessage();
        sender.setTo(String.valueOf(aes.decrypt(userEmail)));
        sender.setSubject(subject);
        sender.setText(text);

        emailSender.send(sender);
    }

/*
    public void sendVerificationMail(UserInfo userInfo) {

        redisUtil.setDataExpire(String.valueOf(userInfo.getUserId()), String.valueOf(userInfo.getUserId()), 1000L * 60 * 60 * 24); // 코드는 24시간  유지됌

        sendMailMessage(userInfo.getUserEmail(), EMAIL_SUB, EMAIL_LINK + userInfo.getUserId());

    }*/


    @SneakyThrows
    public void sendTempPassword(String email, String emailType) {

        email = aes.encrypt(email);

        UserInfo findUser = userRepository.findByUserEmailAndUserEmailType(email, emailType).orElseThrow(CUserNotFoundException::new);

        String uuid = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거해 주었다.
        uuid = uuid.substring(0, 8);

        HashCode md5 = Hashing.md5().hashString(uuid, Charsets.UTF_8);


        findUser.setUserPassword(passwordEncoder.encode(md5.toString()));

        List<String> role = findUser.getRoles();
        role.add("ROLE_TEMP_PASSWORD");
        findUser.setRoles(role);

        findUser.setUserEmail(aes.decrypt(findUser.getUserEmail()));
        Optional.of(userRepository.save(findUser)).orElseThrow(CUserNotFoundException::new);

        sendMailMessage(email, "임시비밀번호를 발급해드립니다.", "임시비밀번호:" + uuid);
    }


    @Transactional
    public void newVerifyEmail(String uniqueCode){

        UserEmailVerify userEmailVerify = userEmailVerifyRepository.findByUniqueCode(uniqueCode).orElseThrow(CUserNotFoundException::new);
        UserInfo userInfo = userRepository.findByUserId(userEmailVerify.getUserId()).orElseThrow(CUserNotFoundException::new);
        userInfo.decryptInfo();
        userInfo.setRoles(Collections.singletonList("ROLE_USER"));
        UserInfo saveAfterUser = userRepository.save(userInfo);
        userEmailVerifyRepository.delete(userEmailVerify);

        Optional.of(wakeUpPermissionRepository.save(new WakeupPermission(saveAfterUser, uniqueCode))).orElseThrow(CResourceNotExistException::new);



    }

  /*  public void verifyEmail(String key) throws CUserNotFoundException {



        UUID userId = UUID.fromString(redisUtil.getData(key));
        UserInfo user = userRepository.findByUserId(userId).orElseThrow(CUserNotFoundException::new);

        user.setRoles(Collections.singletonList("ROLE_USER"));

        UserInfo saveAfterUser = Optional.of(userRepository.save(user)).orElseThrow(CUserNotFoundException::new);
        redisUtil.deleteData(key);


        *//* wakeuf permission테이블에 userId를 저장해준다.*//*
        Optional.of(wakeUpPermissionRepository.save(new WakeupPermission(saveAfterUser, createUniqueCode(user.getUserId())))).orElseThrow(CResourceNotExistException::new);


    }*/

    public UserInfo modifyUserRole(UserInfo userInfo, String role) {

        List<String> userRole = userInfo.getRoles();
        userRole.add(role);
        userInfo.setRoles(userRole);
        return userRepository.save(userInfo);
    }


    public List<UserInfo> getAllUserService() {
        List<UserInfo> userInfoList = userRepository.findAll();
        userInfoList.forEach(UserInfo::decryptInfo);
        return userInfoList;
    }


    public UserInfo userUpdateService(UserInfo userInfo) {

        if (!Strings.isNullOrEmpty(userInfo.getUserPassword())){
        userInfo.setUserPassword(passwordEncoder.encode(userInfo.getUserPassword()));
        }

        UserInfo findUser = Optional.of(findByUserId(userInfo.getUserId())).orElseThrow(CUserNotFoundException::new);
                 findUser.modifyUser(userInfo,userInfo.getUserPassword());

        return userRepository.save(findUser);
    }

    public UserInterestTag userInterestTagService(UserInterestTag userTags) {

        return Optional.of(userInterestTagRepository.save(userTags)).orElseThrow(CUserNotFoundException::new);
    }

    public Optional<UserInterestTag> getUserInterestTag(UUID uuid) {

        return userInterestTagRepository.findByUserId(uuid);

    }


    @Override
    public UserDetails loadUserByUsername(String s) throws CUserNotFoundException {
        UserInfo user = userRepository.findByUserId(UUID.fromString(s)).orElseThrow(CUserNotFoundException::new);

        user.decryptInfo();

        return user;
    }

    public UserInfo addManager(UserInfo userInfo){
        Preconditions.checkNotNull(userInfo.getUserEmail(),"아이디를 입력해주세요.");
        Preconditions.checkNotNull(userInfo.getUserPassword(),"비밀번호를 입력해주세요.");
        Preconditions.checkNotNull(userInfo.getUserAddress(),"이메일을 입력해주세요.");

        userInfo.setUserPassword(passwordEncoder.encode(userInfo.getUserPassword()));

        return userRepository.insert(userInfo);

    }


    public UserInfo modifyManager(UserInfo userInfo){

        if (!Strings.isNullOrEmpty(userInfo.getPassword()))
        userInfo.setUserPassword(passwordEncoder.encode(userInfo.getUserPassword()));

        UserInfo before = findByUserId(userInfo.getUserId());

        before.modifyUser(userInfo,userInfo.getUserPassword());
        return userRepository.save(before);

    }

    public boolean deleteManager(UserInfo userInfo){

        userRepository.delete(userInfo);

        return true;
    }



}
