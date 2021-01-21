package com.kctv.api.service;

import com.google.common.base.Strings;
import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.entity.place.MenuByPlace;
import com.kctv.api.entity.place.PlaceTypeEntity;
import com.kctv.api.entity.stylecard.PartnersByTags;

import com.kctv.api.entity.stylecard.Tag;
import com.kctv.api.repository.ap.MenuByPartnerRepository;
import com.kctv.api.repository.ap.PartnerByTagsRepository;
import com.kctv.api.repository.tag.PlaceTypeRepository;
import com.kctv.api.util.GeoOperations;
import com.kctv.api.entity.place.PlaceInfo;
import com.kctv.api.entity.place.WifiInfo;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.ap.WifiRepository;
import com.kctv.api.util.MapUtill;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.cassandra.config.CassandraCqlTemplateFactoryBean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final WifiRepository wifiRepository;
    private final PartnerRepository partnerRepository;
    private final PartnerByTagsRepository partnerByTagsRepository;

    private final MenuByPartnerRepository menuByPartnerRepository;
    private final PlaceTypeRepository placeTypeRepository;



    public boolean deleteTag(PlaceTypeEntity placeTypeEntity){

        if (partnerByTagsRepository.findByTag(placeTypeEntity.getPlaceType()).size() != 0) {
            return false;
        }else {
            placeTypeRepository.delete(placeTypeEntity);
            return true;
        }
    }


    public Slice<PlaceInfo> pageableFindAllBy(Pageable pageable){



        return partnerRepository.findAll(pageable);
    }

    public List<PlaceInfo> getPlaceListByIdIn(List<UUID> placeIds){

        return partnerRepository.findByPartnerIdIn(placeIds);

    }

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

    public Optional<PlaceInfo> getPartnerByIdService(UUID uuid){

        return partnerRepository.findByPartnerId(uuid);
    }

    public List<PlaceInfo> getPartnerInfoListService(){

        return partnerRepository.findAll();
    }

    public List<PlaceInfo> getPartnerInfoListByTagsService(List<String> tags){


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

            List<PlaceInfo> placeInfos = partnerRepository.findByPartnerIdIn(queryList);

            List<PlaceInfo> resultList = new ArrayList<>();
            for (int i = 0; i < placeInfos.size(); i++) {
                for (int j = 0; j < placeInfos.size(); j++) {
                    if(placeInfos.get(j).getPartnerId().equals(queryList.get(i)))
                        resultList.add(placeInfos.get(j));
                }
            }

            return resultList;
        } else {
            return new ArrayList<PlaceInfo>();
        }

    }
    @Transactional
    public PlaceInfo createPlace(PlaceInfo placeInfo,List<MenuByPlace> menuByPlaceList) {

        placeInfo.setPartnerId(UUID.randomUUID());


        List<PartnersByTags> partnersByTagsList = placeInfo.getTags().stream()
                .map(s -> PartnersByTags.builder()
                        .tag(s)
                        .partnerId(placeInfo.getPartnerId())
                        .build())
                .collect(Collectors.toList());

        partnerByTagsRepository.saveAll(partnersByTagsList);

        if(CollectionUtils.isNotEmpty(menuByPlaceList)) {
            for (MenuByPlace menuByPlace : menuByPlaceList) {
                menuByPlace.setPartnerId(placeInfo.getPartnerId());
            }
            menuByPartnerRepository.saveAll(menuByPlaceList);
        }



       return partnerRepository.insert(placeInfo);
    }
/*

    public List<PartnerInfo> newGetPartnerInfoListByTagsService(List<String> tags){


        //  long score = card.getTags().stream().map(s -> TagGroup.findByTagPoint(s)).reduce(0L,Long::sum);


        List<PartnersByTags> result = partnerByTagsRepository.findByTagIn(tags);

        TagGroup.findByTagPoint(result.get(0).getTag());

        List<PartnerInfo> partnerInfos = partnerRepository.findByPartnerIdIn(queryList);

            return null;
    }*/


    @Transactional
    public PlaceInfo modifyPlace(PlaceInfo requestPlace){

        PlaceInfo beforePlace = partnerRepository.findByPartnerId(requestPlace.getPartnerId()).orElseThrow(CResourceNotExistException::new);

        if(CollectionUtils.isNotEmpty(requestPlace.getAges()))
        beforePlace.setAges(requestPlace.getAges());
        if(!Strings.isNullOrEmpty(requestPlace.getBusinessName()))
        beforePlace.setBusinessName(requestPlace.getBusinessName());
        if(CollectionUtils.isNotEmpty(requestPlace.getFacilities()))
        beforePlace.setFacilities(requestPlace.getFacilities());
        if(!Strings.isNullOrEmpty(requestPlace.getPartnerAddress()))
        beforePlace.setPartnerAddress(requestPlace.getPartnerAddress());
        if(CollectionUtils.isNotEmpty(requestPlace.getPeriods()))
        beforePlace.setPeriods(requestPlace.getPeriods());
        if(CollectionUtils.isNotEmpty(requestPlace.getPartnerHomepage()))
        beforePlace.setPartnerHomepage(requestPlace.getPartnerHomepage());
        if(!Strings.isNullOrEmpty(requestPlace.getStoreType()))
        beforePlace.setStoreType(requestPlace.getStoreType());
        if(CollectionUtils.isNotEmpty(requestPlace.getTags()))
        beforePlace.setTags(requestPlace.getTags());
        if(!Strings.isNullOrEmpty(requestPlace.getTelNumber()))
        beforePlace.setTelNumber(requestPlace.getTelNumber());
        if(!Strings.isNullOrEmpty(requestPlace.getStoreParentType()))
        beforePlace.setStoreParentType(requestPlace.getStoreParentType());


        PlaceInfo afterPlace = Optional.ofNullable(partnerRepository.save(beforePlace)).orElseThrow(CResourceNotExistException::new);

        partnerByTagsRepository.saveAll(
                afterPlace.getTags().stream()
                        .map(s -> new PartnersByTags(s,afterPlace.getPartnerId()))
                        .collect(Collectors.toList()));


        return afterPlace;

    }

    public List<PlaceTypeEntity> getTagListAllService (){
        return placeTypeRepository.findAll();
    }

    public Optional<PlaceTypeEntity> getTagOneService(PlaceTypeEntity placeTypeEntity){return placeTypeRepository.findByPlaceParentTypeAndPlaceType(placeTypeEntity.getPlaceParentType(),placeTypeEntity.getPlaceType());}

    public List<PlaceTypeEntity> getTagList (String search){
        return placeTypeRepository.findByPlaceParentType(search);
    }

    public Optional<PlaceTypeEntity> createTagService(PlaceTypeEntity tag){
        return Optional.ofNullable(placeTypeRepository.insert(tag));
    }



}
