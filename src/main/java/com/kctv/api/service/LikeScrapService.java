package com.kctv.api.service;


import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.advice.exception.CUserExistException;
import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.entity.ap.PartnerInfo;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.entity.user.UserInterestTag;
import com.kctv.api.entity.user.UserLikePartner;
import com.kctv.api.entity.user.UserScrapCard;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import com.kctv.api.repository.user.UserInterestTagRepository;
import com.kctv.api.repository.user.UserLikeRepository;
import com.kctv.api.repository.user.UserRepository;
import com.kctv.api.repository.user.UserScrapRepository;
import com.kctv.api.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeScrapService {

    private final UserLikeRepository userLikeRepository;
    private final PartnerRepository partnerRepository;
    private final UserScrapRepository userScrapRepository;
    private final StyleCardRepository styleCardRepository;

    /* 좋아요 기능*/
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

        if (partnerList.isEmpty()){
            return new ArrayList<PartnerInfo>();
        }else {
        return partnerRepository.findByPartnerIdIn(partnerList);
        }
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

        if(cardList.isEmpty()){
            return new ArrayList<StyleCardInfo>();
        }else {
            return styleCardRepository.findByCardIdIn(cardList);
        }
    }



}
