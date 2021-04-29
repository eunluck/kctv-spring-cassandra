package com.kctv.api.service;


import com.kctv.api.model.place.PlaceInfoEntity;
import com.kctv.api.model.stylecard.StyleCardInfoEntity;
import com.kctv.api.model.user.UserLikePartnerEntity;
import com.kctv.api.model.user.UserScrapCardEntity;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.ap.PlaceCounterDayRepository;
import com.kctv.api.repository.card.StyleCardCounterDayRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import com.kctv.api.repository.user.UserLikeRepository;
import com.kctv.api.repository.user.UserScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeScrapService {

    private final UserLikeRepository userLikeRepository;
    private final PartnerRepository partnerRepository;
    private final UserScrapRepository userScrapRepository;
    private final StyleCardRepository styleCardRepository;
    private final PlaceCounterDayRepository placeCounterDayRepository;
    private final StyleCardCounterDayRepository counterRepository;
    /* 좋아요 기능*/
    public Optional<UserLikePartnerEntity> userLikePartnerService(UUID userId, UUID placeId){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate now = LocalDate.now();
        Long nowToLong = Long.valueOf(now.format(formatter));


        if(likeCheck(userId,placeId)){
            userLikeRepository.delete(UserLikePartnerEntity.builder().partnerId(placeId).userId(userId).build());
            placeCounterDayRepository.decrementLikeCountByPlaceId(nowToLong,placeId);

            return Optional.empty();
        }else{
            placeCounterDayRepository.incrementViewCountByPlaceId(nowToLong,placeId);
            userLikeRepository.save(UserLikePartnerEntity.builder().partnerId(placeId).userId(userId).build());


            return Optional.of(userLikeRepository.save(UserLikePartnerEntity.builder().partnerId(placeId).userId(userId).build()));
        }
    }


    public Boolean likeCheck(UUID userId, UUID placeId){
        return userLikeRepository.findByUserIdAndPartnerId(userId,placeId).isPresent();

    }

    public List<PlaceInfoEntity> likeList(UUID userId){


        List<UserLikePartnerEntity> likeList = userLikeRepository.findByUserId(userId);

        List<UUID> partnerList = likeList.stream().map(UserLikePartnerEntity::getPartnerId)
                                                  .collect(Collectors.toList());

        if (partnerList.isEmpty()){
            return new ArrayList<>();
        }else {
        return partnerRepository.findByPartnerIdIn(partnerList);
        }
    }


    /*유저 스크랩 기능*/
    public Optional<UserScrapCardEntity> userScrapCardService(UUID userId, UUID cardId){


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate now = LocalDate.now();
        Long nowToLong = Long.valueOf(now.format(formatter));


        if(scrapCheck(userId,cardId)){
            userScrapRepository.delete(UserScrapCardEntity.builder().cardId(cardId).userId(userId).build());

            counterRepository.decrementScrapCountByCardId(nowToLong,cardId);
            return Optional.empty();
        }else{
            counterRepository.incrementScrapCountByCardId(nowToLong,cardId);
            return Optional.of(userScrapRepository.save(UserScrapCardEntity.builder().cardId(cardId).userId(userId).build()));
        }
    }

    public Boolean scrapCheck(UUID userId, UUID cardId){
        return userScrapRepository.findByUserIdAndCardId(userId,cardId).isPresent();
    }



    public List<StyleCardInfoEntity> scrapList(UUID userId){


        List<UserScrapCardEntity> scrapList = userScrapRepository.findByUserId(userId);
        List<UUID> cardList = scrapList.stream().map(UserScrapCardEntity::getCardId)
                .collect(Collectors.toList());


        if(cardList.isEmpty()){
            return new ArrayList<>();
        }else {
            return styleCardRepository.findByCardIdIn(cardList);
        }
    }



}
