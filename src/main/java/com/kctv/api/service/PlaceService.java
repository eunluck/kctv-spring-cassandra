package com.kctv.api.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.advice.exception.CRequiredValueException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.entity.place.*;
import com.kctv.api.entity.stylecard.PartnersByTags;

import com.kctv.api.entity.stylecard.Tag;
import com.kctv.api.repository.ap.MenuByPartnerRepository;
import com.kctv.api.repository.ap.PartnerByTagsRepository;
import com.kctv.api.repository.file.CardImageInfoRepository;
import com.kctv.api.repository.tag.PlaceTypeRepository;
import com.kctv.api.util.GeoOperations;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.ap.WifiRepository;
import com.kctv.api.util.MapUtill;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.cassandra.config.CassandraCqlTemplateFactoryBean;
import org.springframework.data.cassandra.core.CassandraBatchOperations;
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
    private final StorageService storageService;
    private final CardImageInfoRepository cardImageInfoRepository;
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


    public PlaceInfo deletePlace(PlaceInfo placeInfo){

        partnerRepository.delete(placeInfo);
        if(CollectionUtils.isNotEmpty(placeInfo.getTags()))
        partnerByTagsRepository.deleteAll(placeInfo.getTags().stream().map(s -> new PartnersByTags(s,placeInfo.getPartnerId())).collect(Collectors.toList()));
    return placeInfo;

    }

    public Slice<PlaceInfo> pageableFindAllBy(Pageable pageable){



        return partnerRepository.findAll(pageable);
    }

    public List<PlaceInfo> getPlaceListByIdIn(List<UUID> placeIds){

        System.out.println("디버깅중::"+placeIds);

        return partnerRepository.findByPartnerIdIn(placeIds);

    }


    public Map<String, List<MenuByPlace>> getMenuByPartnerId(UUID partnerId){

        List<MenuByPlace> menuList =  menuByPartnerRepository.findByPartnerId(partnerId);


        return menuList.stream().collect(Collectors.groupingBy(MenuByPlace::getMenuType));

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
            List<UUID> queryList = new ArrayList<>(sorting.keySet());

            List<PlaceInfo> placeInfos = partnerRepository.findByPartnerIdIn(queryList);

            List<PlaceInfo> resultList = new ArrayList<>();
            for (int i = 0; i < placeInfos.size(); i++) {
                for (PlaceInfo placeInfo : placeInfos) {
                    if (placeInfo.getPartnerId().equals(queryList.get(i)))
                        resultList.add(placeInfo);
                }
            }

            return resultList;
        } else {
            return new ArrayList<>();
        }

    }
    @Transactional
    public PlaceInfo createPlace(PlaceInfo placeInfo,List<MenuByPlace> menuByPlaceList) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(placeInfo.getBusinessName()),new CRequiredValueException("장소 이름을 입력해주세요."));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(placeInfo.getPartnerAddress()),new CRequiredValueException("주소를 입력해주세요."));
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(placeInfo.getTags()),new CRequiredValueException("장소 태그를 입력해주세요."));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(placeInfo.getStoreType()),new CRequiredValueException("장소의 종류를 입력해주세요."));


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
    public PlaceInfoDto modifyPlace(PlaceInfo requestPlace,PlaceInfo beforePlace, List<MenuByPlace> menuByPlaceList){


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
        if(!Strings.isNullOrEmpty(requestPlace.getDetailed_address()))
        beforePlace.setDetailed_address(requestPlace.getDetailed_address());
        if(requestPlace.getLatitude() != null && requestPlace.getLatitude() != 0)
        beforePlace.setLatitude(requestPlace.getLatitude());
        if(requestPlace.getLongitude() != null && requestPlace.getLongitude() != 0)
        beforePlace.setLongitude(requestPlace.getLongitude());
        if(CollectionUtils.isNotEmpty(requestPlace.getImages())){
            beforePlace.setImages(requestPlace.getImages());
        }
        if(CollectionUtils.isNotEmpty(menuByPlaceList)){
            menuByPartnerRepository.deleteAll(menuByPartnerRepository.findByPartnerId(beforePlace.getPartnerId()));
            menuByPlaceList.forEach(menuByPlace -> menuByPlace.setPartnerId(beforePlace.getPartnerId()));
            menuByPartnerRepository.saveAll(menuByPlaceList);
        }

        PlaceInfo afterPlace = Optional.of(partnerRepository.save(beforePlace)).orElseThrow(CResourceNotExistException::new);

        if(CollectionUtils.isNotEmpty(requestPlace.getTags())){
            partnerByTagsRepository.saveAll(
                    afterPlace.getTags().stream()
                            .map(s -> new PartnersByTags(s,afterPlace.getPartnerId()))
                            .collect(Collectors.toList()));
        }

        return new PlaceInfoDto(afterPlace,getMenuByPartnerId(afterPlace.getPartnerId()));


    }

    public List<PlaceTypeEntity> getTagListAllService (){
        return placeTypeRepository.findAll();
    }

    public Optional<PlaceTypeEntity> getTagOneService(PlaceTypeEntity placeTypeEntity){return placeTypeRepository.findByPlaceParentTypeAndPlaceType(placeTypeEntity.getPlaceParentType(),placeTypeEntity.getPlaceType());}

    public List<PlaceTypeEntity> getTagList (String search){
        return placeTypeRepository.findByPlaceParentType(search);
    }

    public Optional<PlaceTypeEntity> createTagService(PlaceTypeEntity tag){
        return Optional.of(placeTypeRepository.insert(tag));
    }



}
