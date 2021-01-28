package com.kctv.api.service;

import com.google.common.base.Strings;
import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.entity.stylecard.StyleCardByTags;
import com.kctv.api.entity.stylecard.StyleCardInfo;
import com.kctv.api.entity.stylecard.Tag;
import com.kctv.api.entity.stylecard.admin.StyleCardVo;
import com.kctv.api.model.tag.TagGroup;
import com.kctv.api.repository.ap.PartnerByTagsRepository;
import com.kctv.api.repository.card.StyleByTagsRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import com.kctv.api.repository.tag.TagRepository;
import com.kctv.api.util.sorting.SortingTagsUtiil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StyleCardService {


    private final StyleCardRepository styleCardRepository;
    private final StyleByTagsRepository styleByTagsRepository;
    private final TagRepository tagRepository;
    private final PartnerByTagsRepository partnerByTagsRepository;

    public List<StyleCardInfo> getStyleCardListAllService(){
        return styleCardRepository.findAll();
    }
    public List<Tag> getTagListAllService (){
        return tagRepository.findAll();
    }
    public Optional<Tag> getTagOneService(Tag tag){return tagRepository.findByTagTypeAndTagName(tag.getTagType(),tag.getTagName());}

    public List<Tag> getTagList (String search){
        return tagRepository.findByTagType(search);
    }

    public StyleCardInfo getCardById (UUID uuid){

        StyleCardInfo card = styleCardRepository.findByCardId(uuid).orElseThrow(CPartnerNotFoundException::new);

        long score = card.getTags().stream().map(TagGroup::findByTagPoint).reduce(0L,Long::sum);

        System.out.println(score);
        return card;
    }

    public Tag createTagService(Tag tag){
        return tagRepository.insert(tag);
    }

    public StyleCardInfo modifyStyleCard(UUID cardId,StyleCardInfo request){

        StyleCardInfo beforeCard = styleCardRepository.findByCardId(cardId).orElseThrow(CResourceNotExistException::new);

        if (CollectionUtils.isNotEmpty(request.getAges()))
        beforeCard.setAges(request.getAges());
        if (!Strings.isNullOrEmpty(request.getCuratorSaying()))
        beforeCard.setCuratorSaying(request.getCuratorSaying());
        if (CollectionUtils.isNotEmpty(request.getGender()))
        beforeCard.setGender(request.getGender());
        if (CollectionUtils.isNotEmpty(request.getPlaceId()))
        beforeCard.setPlaceId(request.getPlaceId());
        if (CollectionUtils.isNotEmpty(request.getTags()))
        beforeCard.setTags(request.getTags());
        if (!Strings.isNullOrEmpty(request.getTitle()))
        beforeCard.setTitle(request.getTitle());
        beforeCard.setModifyAt(new Date());
        beforeCard.setStatus("업데이트");

        StyleCardInfo afterCard = Optional.of(styleCardRepository.save(beforeCard)).orElseThrow(CResourceNotExistException::new);

        if (CollectionUtils.isNotEmpty(request.getTags())){
            styleByTagsRepository.saveAll(
                    request.getTags()
                            .stream()
                            .map(s -> new StyleCardByTags(s,afterCard.getCardId()))
                            .collect(Collectors.toList()));
        }
        return afterCard;
    }






    public List<StyleCardInfo> getCardByTagsService(List<String> tags){
        List<StyleCardByTags> result = styleByTagsRepository.findByTagIn(tags); //태그를 조건으로 StyleCard를 검색
        List<UUID> uuidList = SortingTagsUtiil.duplicationMappingList(result);  //검색된 카드의 태그가 중복되는 갯수 순서로 내림차순 정렬

        List<StyleCardInfo> styleCardInfos = styleCardRepository.findByCardIdIn(uuidList);  // 위 태그에 충족되는 카드들을 UUID를 통해 조회
        return SortingTagsUtiil.SortingToList(styleCardInfos,uuidList); // 중복되는 순서로 내림차순 정렬



/*
        List<StyleCardByTags> result = styleByTagsRepository.findByTagIn(tags);

        ArrayList<UUID> idArr = new ArrayList<>();
        //ArrayList<Map<UUID,Integer>> idArr = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(result)){
            result.forEach(styleCardByTags -> idArr.add(styleCardByTags.getCardId()));
            Map<UUID,Integer> sorting = new HashMap<>();

            for(UUID id:idArr){
                sorting.put(id,sorting.getOrDefault(id,0)+1);
            }

            sorting = MapUtill.sortByValueDesc(sorting);
            List<UUID> queryList = new ArrayList<>();
            queryList.addAll(sorting.keySet());

            List<StyleCardInfo> styleCardInfos = styleCardRepository.findByCardIdIn(queryList);

            List<StyleCardInfo> resultList = new ArrayList<>();
            for (int i = 0; i < styleCardInfos.size(); i++) {
                for (int j = 0; j < styleCardInfos.size(); j++) {
                   if(styleCardInfos.get(j).getCardId().equals(queryList.get(i)))
                     resultList.add(styleCardInfos.get(j));
                }
            }

            return resultList;
        } else {
            return new ArrayList<StyleCardInfo>();
        }
*/

    }



//admin
    @Transactional
    public StyleCardInfo createStyleCard(StyleCardVo styleCardVo){
        StyleCardInfo styleCardInfo = new StyleCardInfo(styleCardVo);

        List<StyleCardByTags> list = styleCardInfo.getTags()
                                    .stream()
                                    .map(s -> new StyleCardByTags(s,styleCardInfo.getCardId()))
                                    .collect(Collectors.toList());

        styleByTagsRepository.saveAll(list);

        return Optional.of(styleCardRepository.save(styleCardInfo)).orElseThrow(RuntimeException::new);
    }



    public boolean deleteTag(Tag tag){

        if (partnerByTagsRepository.findByTag(tag.getTagName()).size() != 0) {
            return false;
        }else {
            tagRepository.delete(tag);
            return true;
        }
    }


}
