package com.kctv.api.service;


import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.advice.exception.CUserExistException;
import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.entity.ap.PartnerInfo;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.entity.user.UserInterestTag;
import com.kctv.api.entity.user.UserLikePartner;
import com.kctv.api.entity.user.UserScrapCard;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import com.kctv.api.repository.user.UserInterestTagRepository;
import com.kctv.api.repository.user.UserLikeRepository;
import com.kctv.api.repository.user.UserScrapRepository;
import com.kctv.api.util.RedisUtil;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final StyleCardRepository styleCardRepository;
    private final UserScrapRepository userScrapRepository;
    private final PartnerRepository partnerRepository;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserInterestTagRepository userInterestTagRepository;
    private final UserLikeRepository userLikeRepository;
    private final String EMAIL_LINK;
    //private final String EMAIL_LINK = "http://192.168.0.56:8081/v1/verify/";
    private final String EMAIL_SUB = "KCTV 회원가입 인증 메일입니다.";


    public UserService(StyleCardRepository styleCardRepository, UserScrapRepository userScrapRepository, PartnerRepository partnerRepository,  RedisUtil redisUtil, UserRepository userRepository, EmailService emailService, UserInterestTagRepository userInterestTagRepository, UserLikeRepository userLikeRepository, @Value("${costom.host.path}") String email_link) {
        this.styleCardRepository = styleCardRepository;
        this.userScrapRepository = userScrapRepository;
        this.partnerRepository = partnerRepository;
        this.redisUtil = redisUtil;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userInterestTagRepository = userInterestTagRepository;
        this.userLikeRepository = userLikeRepository;
        this.EMAIL_LINK = email_link;
    }

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

        redisUtil.setDataExpire(String.valueOf(userInfo.getUserId()),String.valueOf(userInfo.getUserId()),1000L * 60 * 60 * 24 ); // 코드는 5분동안 유지됌
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

    /*유저 좋아요 기능*/
    public Optional<UserLikePartner> userLikePartnerService(UUID userId, UUID placeId){

        if(likeCheck(userId,placeId)){
            userLikeRepository.delete(UserLikePartner.builder().partnerId(placeId).userId(userId).build());
            return Optional.empty();
        }else{
           return Optional.ofNullable(userLikeRepository.save(UserLikePartner.builder().partnerId(placeId).userId(userId).build()));
        }

    }

    public Boolean likeCheck(UUID userId, UUID placeId){

        return userLikeRepository.findByUserIdAndPartnerId(userId,placeId).isPresent();

    }

    public List<PartnerInfo> likeList(UUID userId){

        List<UserLikePartner> likeList = userLikeRepository.findByUserId(userId);

        List<UUID> partnerList = likeList.stream().map(UserLikePartner::getPartnerId)
                                                  .collect(Collectors.toList());

        return partnerRepository.findByPartnerIdIn(partnerList);
    }


    /*유저 스크랩 기능*/
    public Optional<UserScrapCard> userScrapCardService(UUID userId, UUID cardId){

        if(scrapCheck(userId,cardId)){
            userScrapRepository.delete(UserScrapCard.builder().cardId(cardId).userId(userId).build());
            return Optional.empty();
        }else{
            return Optional.ofNullable(userScrapRepository.save(UserScrapCard.builder().cardId(cardId).userId(userId).build()));
        }

    }

    public Boolean scrapCheck(UUID userId, UUID cardId){

        return userScrapRepository.findByUserIdAndCardId(userId,cardId).isPresent();

    }






    public List<StyleCardInfo> scrapList(UUID userId){

        List<UserScrapCard> scrapList = userScrapRepository.findByUserId(userId);

        List<UUID> cardList = scrapList.stream().map(UserScrapCard::getCardId)
                .collect(Collectors.toList());

        return styleCardRepository.findByCardIdIn(cardList);
    }



}
