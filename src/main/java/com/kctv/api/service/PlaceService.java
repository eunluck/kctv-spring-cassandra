package com.kctv.api.service;

import com.google.common.collect.Lists;
import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.entity.partner.MenuByPlace;
import com.kctv.api.entity.partner.MenuVo;
import com.kctv.api.entity.tag.PartnersByTags;
import com.kctv.api.entity.tag.StyleCardByTags;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.entity.tag.Tag;
import com.kctv.api.entity.user.UserLikePartner;

import com.kctv.api.model.request.TagGroup;
import com.kctv.api.repository.ap.MenuByPartnerRepository;
import com.kctv.api.repository.ap.PartnerByTagsRepository;
import com.kctv.api.repository.user.UserLikeRepository;
import com.kctv.api.util.GeoOperations;
import com.kctv.api.entity.ap.PartnerInfo;
import com.kctv.api.entity.ap.WifiInfo;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.ap.WifiRepository;
import com.kctv.api.util.MapUtill;
import com.kctv.api.util.sorting.SortingTagsUtiil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final WifiRepository wifiRepository;
    private final PartnerRepository partnerRepository;
    private final PartnerByTagsRepository partnerByTagsRepository;
    private final MenuByPartnerRepository menuByPartnerRepository;

    public Map<String, List<MenuByPlace>> getMenuByPartnerId(UUID partnerId){

        List<MenuByPlace> menuList =  menuByPartnerRepository.findByPartnerId(partnerId);

        Map<String, List<MenuByPlace>> list = menuList.stream().collect(Collectors.groupingBy(MenuByPlace::getMenuType));



        return list;

    }


    public List<WifiInfo> getPartnerWifiService(UUID partnerId, Double distance){

        WifiInfo wifiInfo = wifiRepository.findByPartnerId(partnerId).orElseThrow(CPartnerNotFoundException::new);

        GeoOperations geo = new GeoOperations(wifiInfo.getApLat(),wifiInfo.getApLon());

        double[] geoArr = geo.GenerateBoxCoordinates(distance);

        return wifiRepository.findByApLatGreaterThanAndApLatLessThanAndApLonGreaterThanAndApLonLessThan(geoArr[1],geoArr[0],geoArr[2],geoArr[3]);

    }

    public Optional<PartnerInfo> getPartnerByIdService(UUID uuid){

        return partnerRepository.findByPartnerId(uuid);
    }

    public List<PartnerInfo> getPartnerInfoListService(){

        return partnerRepository.findAll();
    }

    public List<PartnerInfo> getPartnerInfoListByTagsService(List<String> tags){


        //  long score = card.getTags().stream().map(s -> TagGroup.findByTagPoint(s)).reduce(0L,Long::sum);


        List<PartnersByTags> result = partnerByTagsRepository.findByTagIn(tags);


                ArrayList<UUID> idArr = new ArrayList<>();
        //ArrayList<Map<UUID,Integer>> idArr = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(result)){
            result.forEach(partnerByTags -> idArr.add(partnerByTags.getPartnerId()));
            Map<UUID,Integer> sorting = new HashMap<>();

            for(UUID id:idArr){
                sorting.put(id,sorting.getOrDefault(id,0)+1);
            }

            sorting = MapUtill.sortByValueDesc(sorting);
            List<UUID> queryList = new ArrayList<>();
            queryList.addAll(sorting.keySet());

            List<PartnerInfo> partnerInfos = partnerRepository.findByPartnerIdIn(queryList);

            List<PartnerInfo> resultList = new ArrayList<>();
            for (int i = 0; i < partnerInfos.size(); i++) {
                for (int j = 0; j < partnerInfos.size(); j++) {
                    if(partnerInfos.get(j).getPartnerId().equals(queryList.get(i)))
                        resultList.add(partnerInfos.get(j));
                }
            }

            return resultList;
        } else {
            return new ArrayList<PartnerInfo>();
        }

    }
/*

    public List<PartnerInfo> newGetPartnerInfoListByTagsService(List<String> tags){


        //  long score = card.getTags().stream().map(s -> TagGroup.findByTagPoint(s)).reduce(0L,Long::sum);


        List<PartnersByTags> result = partnerByTagsRepository.findByTagIn(tags);

        TagGroup.findByTagPoint(result.get(0).getTag());

        List<PartnerInfo> partnerInfos = partnerRepository.findByPartnerIdIn(queryList);

            return null;
    }*/




}
