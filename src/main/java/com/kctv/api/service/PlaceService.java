package com.kctv.api.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.advice.exception.CRequiredValueException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.model.stylecard.PartnersByTags;

import com.kctv.api.model.place.*;
import com.kctv.api.repository.ap.MenuByPartnerRepository;
import com.kctv.api.repository.ap.PartnerByTagsRepository;
import com.kctv.api.repository.file.CardImageInfoRepository;
import com.kctv.api.repository.interview.OwnerInterviewRepository;
import com.kctv.api.repository.tag.PlaceTypeRepository;
import com.kctv.api.util.GeoOperations;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.ap.WifiRepository;
import com.kctv.api.util.MapUtill;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
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
    private final OwnerInterviewRepository ownerInterviewRepository;



    public boolean deleteTag(PlaceTypeEntity placeTypeEntity) {

        if (partnerByTagsRepository.findByTag(placeTypeEntity.getPlaceType()).size() != 0) {
            return false;
        } else {
            placeTypeRepository.delete(placeTypeEntity);
            return true;
        }
    }


    public PlaceInfoEntity deletePlace(PlaceInfoEntity placeInfoEntity) {

        partnerRepository.delete(placeInfoEntity);
        if (CollectionUtils.isNotEmpty(placeInfoEntity.getTags()))
            partnerByTagsRepository.deleteAll(placeInfoEntity.getTags().stream().map(s -> new PartnersByTags(s, placeInfoEntity.getPartnerId())).collect(Collectors.toList()));
        return placeInfoEntity;

    }

    public Slice<PlaceInfoEntity> pageableFindAllBy(Pageable pageable) {


        return partnerRepository.findAll(pageable);
    }

    public List<PlaceInfoEntity> getPlaceListByIdIn(List<UUID> placeIds) {


        return partnerRepository.findByPartnerIdIn(placeIds);

    }


    public Map<String, List<MenuByPlaceEntity>> getMenuByPartnerId(UUID partnerId) {

        List<MenuByPlaceEntity> menuList = menuByPartnerRepository.findByPartnerId(partnerId);


        return menuList.stream().collect(Collectors.groupingBy(MenuByPlaceEntity::getMenuType));

    }


    public List<WifiInfoEntity> getPartnerWifiService(UUID partnerId, Double distance) {

        WifiInfoEntity wifiInfoEntity = wifiRepository.findByPartnerId(partnerId).orElseThrow(CPartnerNotFoundException::new);

        GeoOperations geo = new GeoOperations(wifiInfoEntity.getApLat(), wifiInfoEntity.getApLon());

        double[] geoArr = geo.GenerateBoxCoordinates(distance);

        return wifiRepository.findByApLatGreaterThanAndApLatLessThanAndApLonGreaterThanAndApLonLessThan(geoArr[1], geoArr[0], geoArr[2], geoArr[3]);

    }

    public Optional<PlaceInfoEntity> getPartnerByIdService(UUID uuid) {

        return partnerRepository.findByPartnerId(uuid);
    }

    public List<PlaceInfoEntity> getPartnerInfoListService() {

        return partnerRepository.findAll();
    }

    public List<PlaceInfoEntity> getPartnerInfoListByTagsService(List<String> tags) {

        //  long score = card.getTags().stream().map(s -> TagGroup.findByTagPoint(s)).reduce(0L,Long::sum);

        List<PartnersByTags> result = partnerByTagsRepository.findByTagIn(tags);

        ArrayList<UUID> idArr = new ArrayList<>();
        //ArrayList<Map<UUID,Integer>> idArr = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(result)) {
            result.forEach(partnerByTags -> idArr.add(partnerByTags.getPartnerId()));
            Map<UUID, Integer> sorting = new HashMap<>();

            for (UUID id : idArr) {
                sorting.put(id, sorting.getOrDefault(id, 0) + 1);
            }

            sorting = MapUtill.sortByValueDesc(sorting);
            List<UUID> queryList = new ArrayList<>(sorting.keySet());

            List<PlaceInfoEntity> placeInfoEntities = partnerRepository.findByPartnerIdIn(queryList);

            List<PlaceInfoEntity> resultList = new ArrayList<>();
            for (int i = 0; i < placeInfoEntities.size(); i++) {
                for (PlaceInfoEntity placeInfoEntity : placeInfoEntities) {
                    if (placeInfoEntity.getPartnerId().equals(queryList.get(i)))
                        resultList.add(placeInfoEntity);
                }
            }

            return resultList;
        } else {
            return new ArrayList<>();
        }

    }

    @Transactional
    public PlaceInfoEntity createPlace(PlaceInfoEntity placeInfoEntity, List<MenuByPlaceEntity> menuByPlaceEntityList) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(placeInfoEntity.getBusinessName()), new CRequiredValueException("장소 이름을 입력해주세요."));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(placeInfoEntity.getPartnerAddress()), new CRequiredValueException("주소를 입력해주세요."));
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(placeInfoEntity.getTags()), new CRequiredValueException("장소 태그를 입력해주세요."));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(placeInfoEntity.getStoreType()), new CRequiredValueException("장소의 종류를 입력해주세요."));

        placeInfoEntity.setPartnerId(UUID.randomUUID());

        List<PartnersByTags> partnersByTagsList = placeInfoEntity.getTags().stream()
                .map(s -> PartnersByTags.builder()
                        .tag(s)
                        .partnerId(placeInfoEntity.getPartnerId())
                        .build())
                .collect(Collectors.toList());

        partnerByTagsRepository.saveAll(partnersByTagsList);

        if (CollectionUtils.isNotEmpty(menuByPlaceEntityList)) {
            for (MenuByPlaceEntity menuByPlaceEntity : menuByPlaceEntityList) {
                menuByPlaceEntity.setPartnerId(placeInfoEntity.getPartnerId());
            }
            menuByPartnerRepository.saveAll(menuByPlaceEntityList);
        }


        return partnerRepository.insert(placeInfoEntity);
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
    public PlaceInfoDto modifyPlace(PlaceInfoEntity requestPlace, PlaceInfoEntity beforePlace, List<MenuByPlaceEntity> menuByPlaceEntityList) {

        beforePlace.modifyEntity(requestPlace);

        if (CollectionUtils.isNotEmpty(menuByPlaceEntityList)) {
            menuByPartnerRepository.deleteAll(menuByPartnerRepository.findByPartnerId(beforePlace.getPartnerId()));
            menuByPlaceEntityList.forEach(menuByPlaceEntity -> menuByPlaceEntity.setPartnerId(beforePlace.getPartnerId()));
            menuByPartnerRepository.saveAll(menuByPlaceEntityList);
        }

        PlaceInfoEntity afterPlace = Optional.of(partnerRepository.save(beforePlace)).orElseThrow(CResourceNotExistException::new);

        if (CollectionUtils.isNotEmpty(requestPlace.getTags())) {
            partnerByTagsRepository.saveAll(
                    afterPlace.getTags().stream()
                            .map(s -> new PartnersByTags(s, afterPlace.getPartnerId()))
                            .collect(Collectors.toList()));
        }

        return new PlaceInfoDto(afterPlace, getMenuByPartnerId(afterPlace.getPartnerId()));
    }

    public List<PlaceTypeEntity> getTagListAllService() {
        return placeTypeRepository.findAll();
    }

    public Optional<PlaceTypeEntity> getTagOneService(PlaceTypeEntity placeTypeEntity) {
        return placeTypeRepository.findByPlaceParentTypeAndPlaceType(placeTypeEntity.getPlaceParentType(), placeTypeEntity.getPlaceType());
    }

    public List<PlaceTypeEntity> getTagList(String search) {
        return placeTypeRepository.findByPlaceParentType(search);
    }

    public Optional<PlaceTypeEntity> createTagService(PlaceTypeEntity tag) {
        return Optional.of(placeTypeRepository.insert(tag));
    }



}
