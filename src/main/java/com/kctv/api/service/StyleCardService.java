package com.kctv.api.service;

import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.entity.tag.StyleCardByTags;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.entity.tag.Tag;
import com.kctv.api.repository.card.StyleByTagsRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import com.kctv.api.repository.card.TagRepository;
import com.kctv.api.util.sorting.SortingTagsUtiil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class StyleCardService {


    private final StyleCardRepository styleCardRepository;
    private final StyleByTagsRepository styleByTagsRepository;
    private final TagRepository tagRepository;


    public List<StyleCardInfo> getStyleCardListAllService(){
        return styleCardRepository.findAll();
    }
    public List<Tag> getTagListAllService (){
        return tagRepository.findAll();
    }
    public Optional<Tag> getTagOneService(Tag tag){return tagRepository.findByTagTypeAndTagName(tag);}

    public List<Tag> getTagList (String search){
        return tagRepository.findByTagType(search);
    }

    public StyleCardInfo getCardById (UUID uuid){
        return styleCardRepository.findByCardId(uuid).orElseThrow(CPartnerNotFoundException::new);
    }

    public Optional<Tag> createTagService(Tag tag){
        return Optional.ofNullable(tagRepository.insert(tag));
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





}
