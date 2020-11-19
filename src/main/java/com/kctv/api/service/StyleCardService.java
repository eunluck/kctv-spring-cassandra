package com.kctv.api.service;

import com.kctv.api.entity.tag.StyleCardByTags;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.entity.tag.Tag;
import com.kctv.api.repository.card.StyleByTagsRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import com.kctv.api.repository.card.TagRepository;
import com.kctv.api.util.MapUtill;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class StyleCardService {


    private final StyleCardRepository styleCardRepository;
    private final StyleByTagsRepository styleByTagsRepository;
    private final TagRepository tagRepository;

    public List<String> getTagList (String search){


        List<Tag> tagList = tagRepository.findByTagType(search);



        return null;
    }

    public List<StyleCardInfo> getCardByTagsService(List<String> tags){

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







    }



}
