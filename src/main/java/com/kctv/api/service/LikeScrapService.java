package com.kctv.api.service;


import com.kctv.api.entity.place.PlaceInfo;
import com.kctv.api.entity.stylecard.StyleCardCounter;
import com.kctv.api.entity.stylecard.StyleCardInfo;
import com.kctv.api.entity.user.UserLikePartner;
import com.kctv.api.entity.user.UserScrapCard;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.card.StyleCardCounterRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import com.kctv.api.repository.user.UserLikeRepository;
import com.kctv.api.repository.user.UserScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraBatchOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
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
    private final StyleCardCounterRepository counterRepository;
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

    public List<PlaceInfo> likeList(UUID userId){


        List<UserLikePartner> likeList = userLikeRepository.findByUserId(userId);

        List<UUID> partnerList = likeList.stream().map(UserLikePartner::getPartnerId)
                                                  .collect(Collectors.toList());

        if (partnerList.isEmpty()){
            return new ArrayList<PlaceInfo>();
        }else {
        return partnerRepository.findByPartnerIdIn(partnerList);
        }
    }


    /*유저 스크랩 기능*/
    public Optional<UserScrapCard> userScrapCardService(UUID userId, UUID cardId){

        if(scrapCheck(userId,cardId)){
            userScrapRepository.delete(UserScrapCard.builder().cardId(cardId).userId(userId).build());
            counterRepository.decrementScrapCountByCardId(cardId);
            return Optional.empty();
        }else{
            counterRepository.incrementScrapCountByCardId(cardId);
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
