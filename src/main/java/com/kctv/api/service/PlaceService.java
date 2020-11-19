package com.kctv.api.service;

import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.entity.tag.PartnersByTags;
import com.kctv.api.entity.tag.StyleCardByTags;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.entity.tag.Tag;
import com.kctv.api.repository.ap.PartnerByTagsRepository;
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

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlaceService {

    final WifiRepository wifiRepository;
    final PartnerRepository partnerRepository;
    final PartnerByTagsRepository partnerByTagsRepository;

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



}
