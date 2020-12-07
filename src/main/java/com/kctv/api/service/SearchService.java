package com.kctv.api.service;

import com.google.common.collect.Lists;
import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.entity.ap.PartnerInfo;
import com.kctv.api.entity.tag.PartnersByTags;
import com.kctv.api.entity.tag.StyleCardByTags;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.entity.tag.Tag;
import com.kctv.api.repository.ap.PartnerByTagsRepository;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.card.StyleByTagsRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import com.kctv.api.repository.card.TagRepository;
import com.kctv.api.util.sorting.SortingTagsUtiil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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



    public List<PartnerInfo> searchPartner(String param){


        List<UUID> uuidList  = partnerByTagsRepository.findByTag(param)
                                .stream()
                                .map(PartnersByTags::getPartnerId)
                                .collect(Collectors.toList());


        List<PartnerInfo> resultList = Lists.newArrayList();

        if (!uuidList.isEmpty()) {
            resultList.addAll(partnerRepository.findByPartnerIdIn(uuidList));
        }

        List<PartnerInfo> searchList = partnerRepository.findByBusinessNameContaining(param);

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
