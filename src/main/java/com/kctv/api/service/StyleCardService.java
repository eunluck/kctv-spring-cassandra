package com.kctv.api.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.model.admin.stylecard.StyleCardVo;
import com.kctv.api.model.stylecard.*;
import com.kctv.api.model.tag.TagEntity;
import com.kctv.api.model.tag.TagGroup;
import com.kctv.api.repository.ap.PartnerByTagsRepository;
import com.kctv.api.repository.card.StyleByTagsRepository;
import com.kctv.api.repository.card.StyleCardCounterDayRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import com.kctv.api.repository.tag.TagRepository;
import com.kctv.api.util.sorting.SortingTagsUtiil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StyleCardService {


    private final StyleCardRepository styleCardRepository;
    private final StyleByTagsRepository styleByTagsRepository;
    private final TagRepository tagRepository;
    private final PartnerByTagsRepository partnerByTagsRepository;
    private final StyleCardCounterDayRepository counterDayRepository;

    public List<StyleCardInfoEntity> getStyleCardListAllService(){
        return styleCardRepository.findAll();
    }
    public List<TagEntity> getTagListAllService (){
        return tagRepository.findAll();
    }
    public Optional<TagEntity> getTagOneService(TagEntity tag){return tagRepository.findByTagTypeAndTagName(tag.getTagType(),tag.getTagName());}
    public void deleteStyleCard(StyleCardInfoEntity styleCardInfoEntity){

        if (CollectionUtils.isNotEmpty(styleCardInfoEntity.getTags()))
        styleByTagsRepository.deleteAll(styleCardInfoEntity.getTags().stream().map(s -> new StyleCardByTags(s, styleCardInfoEntity.getCardId())).collect(Collectors.toList()));
        styleCardRepository.delete(styleCardInfoEntity);
    }
    public List<TagEntity> getTagList (String search){
        return tagRepository.findByTagType(search);
    }

    public StyleCardInfoEntity getCardById (UUID uuid){

        StyleCardInfoEntity card = styleCardRepository.findByCardId(uuid).orElseThrow(CPartnerNotFoundException::new);

        long score = card.getTags().stream().map(TagGroup::findByTagPoint).reduce(0L,Long::sum);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate now = LocalDate.now();
        Long nowToLong = Long.valueOf(now.format(formatter));


        counterDayRepository.incrementViewCountByCardId(nowToLong,card.getCardId());

        return card;
    }

    public TagEntity createTagService(TagEntity tag){
        return tagRepository.insert(tag);
    }

    public StyleCardInfoEntity modifyStyleCard(UUID cardId, StyleCardInfoEntity request){

        StyleCardInfoEntity beforeCard = styleCardRepository.findByCardId(cardId).orElseThrow(CResourceNotExistException::new);

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

        StyleCardInfoEntity afterCard = Optional.of(styleCardRepository.save(beforeCard)).orElseThrow(CResourceNotExistException::new);

        if (CollectionUtils.isNotEmpty(request.getTags())){
            styleByTagsRepository.saveAll(
                    request.getTags()
                            .stream()
                            .map(s -> new StyleCardByTags(s,afterCard.getCardId()))
                            .collect(Collectors.toList()));
        }
        return afterCard;
    }






    public List<StyleCardInfoEntity> getCardByTagsService(List<String> tags){
        List<StyleCardByTags> result = styleByTagsRepository.findByTagIn(tags); //태그를 조건으로 StyleCard를 검색
        List<UUID> uuidList = SortingTagsUtiil.duplicationMappingList(result);  //검색된 카드의 태그가 중복되는 갯수 순서로 내림차순 정렬

        if(uuidList.size() > 0){
        List<StyleCardInfoEntity> styleCardInfoEntities = styleCardRepository.findByCardIdIn(uuidList);  // 위 태그에 충족되는 카드들을 UUID를 통해 조회
        return SortingTagsUtiil.SortingToList(styleCardInfoEntities,uuidList); // 중복되는 순서로 내림차순 정렬
        }else {
            return Lists.newArrayList();
        }



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
    public StyleCardInfoEntity createStyleCard(StyleCardVo styleCardVo){
        Preconditions.checkArgument(styleCardVo.getGender().stream().allMatch(s -> s.equals("male") || s.equals("female")),"성별은 필수값입니다. (허용 되는 값 : male or female)");

        StyleCardInfoEntity styleCardInfoEntity = new StyleCardInfoEntity(styleCardVo);

        List<StyleCardByTags> list = styleCardInfoEntity.getTags()
                                    .stream()
                                    .map(s -> new StyleCardByTags(s, styleCardInfoEntity.getCardId()))
                                    .collect(Collectors.toList());

        styleByTagsRepository.saveAll(list);

        return Optional.of(styleCardRepository.save(styleCardInfoEntity)).orElseThrow(RuntimeException::new);
    }



    public boolean deleteTag(TagEntity tag){

        if (partnerByTagsRepository.findByTag(tag.getTagName()).size() != 0) {
            return false;
        }else {
            tagRepository.delete(tag);
            return true;
        }



    }




    public List<StyleCardCounterEntity> cardCountListByWeek(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate now = LocalDate.now().minusDays(7);
        Long nowToLong = Long.valueOf(now.format(formatter)); //일주일전날짜구함

        System.out.println("현재시간변환"+nowToLong);

    List<StyleCardCounterByDayEntity> list = counterDayRepository.findByWeekCount(nowToLong);

        System.out.println();
        return list.stream()
                .collect(Collectors
                        .groupingBy(styleCardCounterByDayEntity ->

                                styleCardCounterByDayEntity.getKey().getCardId(),TreeMap::new,Collectors.summingLong(value -> value.getViewCount() ==null ? 0 : value.getViewCount())))
                .entrySet().stream().map(uuidLongEntry ->
                        new StyleCardCounterEntity(uuidLongEntry.getKey(),0L,Optional.ofNullable(uuidLongEntry.getValue()).orElseGet(() -> 0L),null,null))
                .collect(Collectors.toList());
    }

    public List<StyleCardInfoEntity> cardInfosByIds(List<UUID> uuids){

        return styleCardRepository.findByCardIdIn(uuids);
    }

}
