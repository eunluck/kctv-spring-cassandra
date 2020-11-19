package com.kctv.api.util.sorting;

import com.kctv.api.entity.tag.StyleCardByTags;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.util.MapUtill;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class SortingTagsUtiil {

    public static List<UUID> duplicationMappingList (List<StyleCardByTags> cardList) {

    //List<StyleCardByTags> result = styleByTagsRepository.findByTagIn(tags);

    ArrayList<UUID> idArr = new ArrayList<>();

            cardList.forEach(styleCardByTags -> idArr.add(styleCardByTags.getCardId()));
        Map<UUID,Integer> sorting = new HashMap<>();

        for(UUID id:idArr){
            sorting.put(id,sorting.getOrDefault(id,0)+1);
        }

        sorting = MapUtill.sortByValueDesc(sorting);
        List<UUID> queryList = new ArrayList<>();
        queryList.addAll(sorting.keySet());



        return queryList;
    }

    public static List<StyleCardInfo> SortingToList (List<StyleCardInfo> cardInfos,List<UUID> queryList) {

        List<StyleCardInfo> resultList = new ArrayList<>();
        for (int i = 0; i < cardInfos.size(); i++) {
            for (int j = 0; j < cardInfos.size(); j++) {
                if(cardInfos.get(j).getCardId().equals(queryList.get(i)))
                    resultList.add(cardInfos.get(j));
            }
        }

        return resultList;
    }

}