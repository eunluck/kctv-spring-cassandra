package com.kctv.api.service;


import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.advice.exception.CUserExistException;
import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.model.visit.UserVisitHistoryEntity;
import com.kctv.api.model.ap.WakeupPermissionEntity;
import com.kctv.api.model.user.*;
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
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.krb5.internal.crypto.Aes256;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
    private final UserInfoByEmailRepository userInfoByEmailRepository;
    private final String EMAIL_LINK;
    //private final String EMAIL_LINK = "http://192.168.0.56:8081/v1/verify/";
    private final String EMAIL_SUB = "[WAKE UF] 이메일 계정을 인증해주세요.";



    public UserService(UserUniqueCodeRepository userUniqueCodeRepository, WakeUpPermissionRepository wakeUpPermissionRepository, @Lazy PasswordEncoder passwordEncoder, UserRepository userRepository, JavaMailSender emailSender, UserInterestTagRepository userInterestTagRepository, UserLikeRepository userLikeRepository, UserScrapRepository userScrapRepository, UserEmailVerifyRepository userEmailVerifyRepository, VisitHistoryRepository visitHistoryRepository, UserInfoByEmailRepository userInfoByEmailRepository, @Value("${costom.host.path}") String email_link) {
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
        this.userInfoByEmailRepository = userInfoByEmailRepository;
        this.EMAIL_LINK = email_link;

    }

    public UserInfoEntity deleteUserInfo(UserInfoEntity userInfoEntity){
        WakeupPermissionEntity userPermission;
        UserUniqueCodeEntity userUniqueCodeEntity;
        UserInterestTagEntity userTags;
        List<UserLikePartnerEntity> userLikePartnerEntities;
        List<UserScrapCardEntity> userScrapCardEntities;
        UserEmailVerifyEntity userEmailVerifyEntity;
        List<UserVisitHistoryEntity> userVisitHistoryEntities;
        UserInfoByEmailEntity userInfoByEmailEntity;
        try {
            userPermission = wakeUpPermissionRepository.findByUserId(userInfoEntity.getUserId()).orElseThrow(CUserNotFoundException::new);
            userUniqueCodeEntity = userUniqueCodeRepository.findByUniqueCode(userPermission.getUniqueCode()).orElseThrow(CUserNotFoundException::new);
            userTags = userInterestTagRepository.findByUserId(userInfoEntity.getUserId()).orElse(null);
            userLikePartnerEntities = userLikeRepository.findByUserId(userInfoEntity.getUserId());
            userScrapCardEntities = userScrapRepository.findByUserId(userInfoEntity.getUserId());
            userEmailVerifyEntity = userEmailVerifyRepository.findByUserId(userInfoEntity.getUserId()).orElse(null);
            userVisitHistoryEntities = visitHistoryRepository.findByUserId(userInfoEntity.getUserId());
            userInfoByEmailEntity = userInfoByEmailRepository.findByUserEmailAndUserEmailType(userInfoEntity.getUserEmail(),userInfoEntity.getUserEmailType()).orElse(null);
            userInfoByEmailEntity.setUserEmail(AES256Util.getInstance().encrypt(userInfoByEmailEntity.getUserEmail()));
        }catch (Exception e){
            e.printStackTrace();
            throw new CUserNotFoundException();
        }

        userRepository.delete(userInfoEntity);
        wakeUpPermissionRepository.delete(userPermission);
        userUniqueCodeRepository.delete(userUniqueCodeEntity);
        if (userTags != null)
            userInterestTagRepository.delete(userTags);
        if (CollectionUtils.isNotEmpty(userLikePartnerEntities))
            userLikeRepository.deleteAll(userLikePartnerEntities);
        if (CollectionUtils.isNotEmpty(userScrapCardEntities))
            userScrapRepository.deleteAll(userScrapCardEntities);
        if(CollectionUtils.isNotEmpty(userVisitHistoryEntities))
            visitHistoryRepository.deleteAll(userVisitHistoryEntities);
        if (userEmailVerifyEntity != null)
            userEmailVerifyRepository.delete(userEmailVerifyEntity);
        if (userInfoByEmailEntity != null)
            userInfoByEmailRepository.delete(userInfoByEmailEntity);

        return userInfoEntity;
    }

    public UserInfoEntity findByUserId(UUID uuid) {

        return userRepository.findByUserId(uuid).orElseThrow(CUserNotFoundException::new);
    }

    public Optional<UserInfoEntity> checkByEmail(String email, String emailType) {

        Optional<UserInfoByEmailEntity> userEmail = userInfoByEmailRepository.findByUserEmailAndUserEmailType(email, emailType);
        //userInfoByEmailRepository.findByUserEmailAndUserEmailType(email, emailType)
        AtomicReference<Optional<UserInfoEntity>> result = new AtomicReference<>(Optional.empty());
        userEmail.ifPresent(userInfoByEmailEntity -> result.set(userRepository.findByUserId(userInfoByEmailEntity.getUserId())));
        return result.get();
    }


    public Optional<UserInfoEntity> userLoginService(String email, String emailType, String pwd) {

        UserInfoEntity loginUser = userInfoByEmailRepository.findByUserEmailAndUserEmailType(email,emailType)
                .map(userInfoByEmailEntity ->
                        userRepository.findByUserId(userInfoByEmailEntity.getUserId())
                                .orElseThrow(CUserNotFoundException::new))
                .orElseThrow(CUserNotFoundException::new);

//        UserInfoEntity loginUser = userRepository.findByUserEmailAndUserEmailType(email, emailType).orElseThrow(CUserNotFoundException::new);


        System.out.println(loginUser.toString());
        //return Optional.empty();
        return passwordEncoder.matches(pwd, loginUser.getUserPassword()) ? Optional.of(loginUser) : Optional.empty();
    }

    public Optional<UserInfoEntity> userSnsLoginService(String snsKey) {

        UUID findId = userInfoByEmailRepository.findByUserSnsKey(snsKey).map(UserInfoByEmailEntity::getUserId).orElse(null);

        return  findId == null ? Optional.empty() : userRepository.findByUserId(findId);

    }

    public String createUniqueCode(UUID userId) {

        String resultCode;

        do {
            String code = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거해 주었다.
            resultCode = code.substring(0, 6);

        } while (userUniqueCodeRepository.findByUniqueCode(resultCode).isPresent());

        return userUniqueCodeRepository.insert(new UserUniqueCodeEntity(resultCode, userId, new Date())).getUniqueCode();

    }

    public UserInfoEntity userSignUpService(UserInfoEntity userInfoEntity) {

        String code = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거해 주었다.
        code = code.substring(0, 6);
        userInfoEntity.setUserId(UUID.randomUUID());
        String userUniqueCode = createUniqueCode(userInfoEntity.getUserId());

        WakeupPermissionEntity wakeupPermissionEntity = null;

        if ("user".equals(userInfoEntity.getUserEmailType())) {
            userInfoEntity.setRoles(Collections.singletonList("ROLE_NOT_VERIFY_EMAIL"));
            userInfoEntity.setUserPassword(passwordEncoder.encode(userInfoEntity.getUserPassword()));
        } else {
            userInfoEntity.setRoles(Collections.singletonList("ROLE_USER"));

            wakeupPermissionEntity = wakeUpPermissionRepository.save(new WakeupPermissionEntity(userInfoEntity, userUniqueCode));
        }

        userInfoEntity.setUserStatus("NORMAL");
        userInfoEntity.setCreateDate(new Date());
        userInfoEntity.setInviteCode(code);

        UserInfoEntity result = userRepository.insert(userInfoEntity);
        UserInfoByEmailEntity userEmail = userInfoByEmailRepository.insert(new UserInfoByEmailEntity(result.getUserEmail(),result.getUserEmailType(),result.getUserId(),result.getUserPassword(),result.getUserSnsKey()));

        if ("user".equals(result.getUserEmailType())){

            try {
                userEmailVerifySave(userUniqueCode, result.getUserId(), result.getUserEmail());

            }catch (Exception e){
                e.printStackTrace();
                userRepository.delete(result);
                wakeUpPermissionRepository.delete(wakeupPermissionEntity);
                userInfoByEmailRepository.delete(userEmail);
                throw new CUserNotFoundException();
            }
        }

        return result;

    }


    public void userEmailVerifySave(String uniqueCode, UUID userId, String email)  {

        System.out.println("저장중");

        UserEmailVerifyEntity result = userEmailVerifyRepository.save(new UserEmailVerifyEntity(uniqueCode,new Date(),email, userId));

        try {
            sendMailMessage(AES256Util.getInstance().decrypt(email), EMAIL_SUB,
                "안녕하세요. WAKE UF입니다.<br>" +
                        "이메일 인증을 받으시려면 24시간 이내에 아래 ‘이메일 인증 받기’를 클릭해주세요.<br>  메일 수신 시간으로부터 24시간이 지나면 본 링크는 만료되며, 다시 이메일 인증을 요청 해야합니다. <br>" +
                        "<br><a href=\""+EMAIL_LINK + uniqueCode+"\">이 곳을 클릭하여 이메일 인증 받기</a>"); //메일전송
        }catch (Exception e){
            System.out.println("저장실패");
            e.printStackTrace();
            userEmailVerifyRepository.delete(result);

        }

    }

    @SneakyThrows
    public void userVerifyEmailResend(UserInfoEntity userInfoEntity) throws UnsupportedEncodingException {

         userEmailVerifyRepository.findByUserId(userInfoEntity.getUserId())
                 .ifPresent(beforeUserEmailVerifyEntity -> userEmailVerifyRepository.delete(beforeUserEmailVerifyEntity));

        userEmailVerifySave(createUniqueCode(userInfoEntity.getUserId()), userInfoEntity.getUserId(), AES256Util.getInstance().encrypt(userInfoEntity.getUserEmail()));

    }

    public void sendMailMessage(String userEmail, String subject, String text) throws MessagingException {
        System.out.println("메일전송");

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,"utf-8");

        helper.setText(text,true);
        helper.setTo(String.valueOf(userEmail));
        helper.setSubject(subject);




        emailSender.send(mimeMessage);
        System.out.println("메일전송완료");
    }

/*
    public void sendVerificationMail(UserInfo userInfo) {

        redisUtil.setDataExpire(String.valueOf(userInfo.getUserId()), String.valueOf(userInfo.getUserId()), 1000L * 60 * 60 * 24); // 코드는 24시간  유지됌

        sendMailMessage(userInfo.getUserEmail(), EMAIL_SUB, EMAIL_LINK + userInfo.getUserId());

    }*/


    public void sendTempPassword(String email, String emailType) throws MessagingException {


        UserInfoEntity findUser = userRepository.findByUserId(userInfoByEmailRepository.findByUserEmailAndUserEmailType(email, emailType).orElseThrow(CUserNotFoundException::new).getUserId()).orElseThrow(CUserNotFoundException::new);

        String uuid = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거해 주었다.
        uuid = uuid.substring(0, 8);

        HashCode md5 = Hashing.md5().hashString(uuid, Charsets.UTF_8);


        findUser.setUserPassword(passwordEncoder.encode(md5.toString()));

        List<String> role = findUser.getRoles();
        role.add("ROLE_TEMP_PASSWORD");
        findUser.setRoles(role);

        userInfoByEmailRepository.save(new UserInfoByEmailEntity(findUser.getUserEmail(),findUser.getUserEmailType(),findUser.getUserId(),findUser.getUserPassword(),findUser.getUserSnsKey()));
        Optional.of(userRepository.save(findUser)).orElseThrow(CUserNotFoundException::new);

        sendMailMessage(findUser.getUserEmail(), "[WAKE UF] 임시 비밀번호가 발급되었습니다.", "안녕하세요. WAKE UF입니다.<br>" +
                "요청하신 임시 비밀번호가 아래와 같이 발급되었습니다. <br>" +
                "임시 비밀번호는 일회성 비밀번호이므로, 로그인 후 비밀번호를 신속하게 변경해 주시기 바랍니다.<br>" +
                "<br>\n" + uuid);
    }


    @Transactional
    public void newVerifyEmail(String uniqueCode){

        UserEmailVerifyEntity userEmailVerifyEntity = userEmailVerifyRepository.findByUniqueCode(uniqueCode).orElseThrow(CUserNotFoundException::new);
        UserInfoEntity userInfoEntity = userRepository.findByUserId(userEmailVerifyEntity.getUserId()).orElseThrow(CUserNotFoundException::new);
        userInfoEntity.setRoles(Collections.singletonList("ROLE_USER"));
        UserInfoEntity saveAfterUser = userRepository.save(userInfoEntity);
        userEmailVerifyRepository.delete(userEmailVerifyEntity);

        Optional.of(wakeUpPermissionRepository.save(new WakeupPermissionEntity(saveAfterUser, uniqueCode))).orElseThrow(CResourceNotExistException::new);


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

    public UserInfoEntity modifyUserRole(UserInfoEntity userInfoEntity, String role) {

        List<String> userRole = userInfoEntity.getRoles();
        userRole.add(role);
        userInfoEntity.setRoles(userRole);
        return userRepository.save(userInfoEntity);
    }


    public List<UserInfoEntity> getAllUserService() {
        return userRepository.findAll();
    }


    public UserInfoEntity userUpdateService(UserInfoEntity userInfoEntity) {

        if (!Strings.isNullOrEmpty(userInfoEntity.getUserPassword())){
        userInfoEntity.setUserPassword(passwordEncoder.encode(userInfoEntity.getUserPassword()));

        UserInfoEntity findUser = Optional.of(findByUserId(userInfoEntity.getUserId())).orElseThrow(CUserNotFoundException::new);
        findUser.modifyUser(userInfoEntity, userInfoEntity.getUserPassword());

        userInfoByEmailRepository.save(new UserInfoByEmailEntity(findUser.getUserEmail(),findUser.getUserEmailType(),findUser.getUserId(),findUser.getUserPassword(),findUser.getUserSnsKey()));
        return userRepository.save(findUser);
        }else {
            UserInfoEntity findUser = Optional.of(findByUserId(userInfoEntity.getUserId())).orElseThrow(CUserNotFoundException::new);
            findUser.modifyUser(userInfoEntity, userInfoEntity.getUserPassword());

        return userRepository.save(findUser);
        }
    }

    public UserInterestTagEntity userInterestTagService(UserInterestTagEntity userTags) {

        return Optional.of(userInterestTagRepository.save(userTags)).orElseThrow(CUserNotFoundException::new);
    }

    public Optional<UserInterestTagEntity> getUserInterestTag(UUID uuid) {

        return userInterestTagRepository.findByUserId(uuid);

    }

    @Override
    public UserDetails loadUserByUsername(String s) throws CUserNotFoundException {


        System.out.println("디버깅loadUserByuserName:::"+s);
        return userRepository.findByUserId(UUID.fromString(s)).orElseThrow(CUserNotFoundException::new);
    }

    public UserInfoEntity addManager(UserInfoEntity userInfoEntity){
        Preconditions.checkNotNull(userInfoEntity.getUserEmail(),"아이디를 입력해주세요.");
        Preconditions.checkNotNull(userInfoEntity.getUserPassword(),"비밀번호를 입력해주세요.");
        Preconditions.checkNotNull(userInfoEntity.getUserAddress(),"이메일을 입력해주세요.");

        userInfoEntity.setUserPassword(passwordEncoder.encode(userInfoEntity.getUserPassword()));

        UserInfoEntity result = userRepository.insert(userInfoEntity);
        userInfoByEmailRepository.insert(new UserInfoByEmailEntity(result.getUserEmail(),result.getUserEmailType(),result.getUserId(),result.getUserPassword(),result.getUserSnsKey()));
        return result;

    }


    public UserInfoEntity modifyManager(UserInfoEntity userInfoEntity){
        if (!Strings.isNullOrEmpty(userInfoEntity.getUserEmail())){
            if(userInfoByEmailRepository.findByUserEmailAndUserEmailType(userInfoEntity.getUserEmail(),"user").isPresent()){
                throw new CUserExistException();
            }
        }

        if (!Strings.isNullOrEmpty(userInfoEntity.getPassword()))
        userInfoEntity.setUserPassword(passwordEncoder.encode(userInfoEntity.getUserPassword()));

        UserInfoEntity before = findByUserId(userInfoEntity.getUserId());

        before.modifyUser(userInfoEntity, userInfoEntity.getUserPassword());
        return userRepository.save(before);

    }

    public boolean deleteManager(UserInfoEntity userInfoEntity){

        userInfoByEmailRepository.delete(new UserInfoByEmailEntity(userInfoEntity.getUserEmail(),userInfoEntity.getUserEmailType(),userInfoEntity.getUserId(),userInfoEntity.getUserPassword(),userInfoEntity.getUserSnsKey()));
        userRepository.delete(userInfoEntity);

        return true;
    }



}
