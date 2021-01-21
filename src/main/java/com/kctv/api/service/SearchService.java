package com.kctv.api.service;

import com.google.common.collect.Lists;
import com.kctv.api.entity.place.PlaceInfo;
import com.kctv.api.entity.stylecard.PartnersByTags;
import com.kctv.api.entity.stylecard.StyleCardByTags;
import com.kctv.api.entity.stylecard.StyleCardInfo;
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



    public List<PlaceInfo> searchPartner(String param){


        List<UUID> uuidList  = partnerByTagsRepository.findByTag(param)
                                .stream()
                                .map(PartnersByTags::getPartnerId)
                                .collect(Collectors.toList());


        List<PlaceInfo> resultList = Lists.newArrayList();

        if (!uuidList.isEmpty()) {
            resultList.addAll(partnerRepository.findByPartnerIdIn(uuidList));
        }

        List<PlaceInfo> searchList = partnerRepository.findByBusinessNameContaining(param);

        resultList.addAll(searchList); //TODO 노출 우선순위, 정렬 기준 정해야함

        return resultList;

    }
    public List<StyleCardInfo> searchCard(String param){


        List<UUID> uuidList  = styleByTagsRepository.findByTag(param)
                                .stream()
                                .map(StyleCardByTags::getCardId)
                                .collect(Collectors.toList());


        List<StyleCardInfo> resultList = Lists.newArrayList();

        if(!uuidList.isEmpty()) {
            resultList.addAll(styleCardRepository.findByCardIdIn(uuidList));
        }

        List<StyleCardInfo> searchList = styleCardRepository.findByTitleContaining(param);

        resultList.addAll(searchList); //TODO 노출 우선순위, 정렬 기준 정해야함

        return resultList;

    }





}
