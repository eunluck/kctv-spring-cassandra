package com.kctv.api.service;

import com.google.common.collect.Lists;
import com.kctv.api.model.place.PlaceInfoEntity;
import com.kctv.api.model.stylecard.PartnersByTags;
import com.kctv.api.model.stylecard.StyleCardByTags;
import com.kctv.api.model.stylecard.StyleCardInfoEntity;
import com.kctv.api.repository.ap.PartnerByTagsRepository;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.card.StyleByTagsRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchService {


    private final PartnerRepository partnerRepository;
    private final PartnerByTagsRepository partnerByTagsRepository;
    private final StyleCardRepository styleCardRepository;
    private final StyleByTagsRepository styleByTagsRepository;
//    private final TagRepository tagRepository;


    public List<PlaceInfoEntity> searchPartner(String param){


        List<UUID> uuidList  = partnerByTagsRepository.findByTag(param)
                                .stream()
                                .map(PartnersByTags::getPartnerId)
                                .collect(Collectors.toList());


        List<PlaceInfoEntity> resultList = Lists.newArrayList();

        if (!uuidList.isEmpty()) {
            resultList.addAll(partnerRepository.findByPartnerIdIn(uuidList));
        }

        List<PlaceInfoEntity> searchList = partnerRepository.findByBusinessNameContaining(param);

        resultList.addAll(searchList); //TODO 노출 우선순위, 정렬 기준 정해야함

        return resultList;

    }
    public List<StyleCardInfoEntity> searchCard(String param){


        List<UUID> uuidList  = styleByTagsRepository.findByTag(param)
                                .stream()
                                .map(StyleCardByTags::getCardId)
                                .collect(Collectors.toList());


        List<StyleCardInfoEntity> resultList = Lists.newArrayList();

        if(!uuidList.isEmpty()) {
            resultList.addAll(styleCardRepository.findByCardIdIn(uuidList));
        }

        List<StyleCardInfoEntity> searchList = styleCardRepository.findByTitleContaining(param);

        resultList.addAll(searchList); //TODO 노출 우선순위, 정렬 기준 정해야함

        return resultList;

    }





}
