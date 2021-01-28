package com.kctv.api.service;


import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.entity.admin.ad.CaptivePortalAdEntity;
import com.kctv.api.repository.ad.CaptivePortalAdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaptivePortalAdService {

    private final CaptivePortalAdRepository captivePortalAdRepository;

    public CaptivePortalAdEntity getAdById(UUID adId){

        return Optional.ofNullable(captivePortalAdRepository.findByAdId(adId)).orElseThrow(CResourceNotExistException::new);

    }

    public List<CaptivePortalAdEntity> findAllAd(){

        return captivePortalAdRepository.findAll();
    }

    public CaptivePortalAdEntity deleteAd(UUID adId){
        CaptivePortalAdEntity deleteRequest = Optional.ofNullable(captivePortalAdRepository.findByAdId(adId)).orElseThrow(CResourceNotExistException::new);

        captivePortalAdRepository.delete(deleteRequest);

        return deleteRequest;
    }


    public CaptivePortalAdEntity activeAd(UUID adId){
        CaptivePortalAdEntity activeRequest = Optional.ofNullable(captivePortalAdRepository.findByAdId(adId)).orElseThrow(CResourceNotExistException::new);

        activeRequest.setAdStatus("Active");


        return captivePortalAdRepository.save(activeRequest);
    }

    public CaptivePortalAdEntity inactiveAd(UUID adId){
        CaptivePortalAdEntity activeRequest = Optional.ofNullable(captivePortalAdRepository.findByAdId(adId)).orElseThrow(CResourceNotExistException::new);

        activeRequest.setAdStatus("Inactive");


        return captivePortalAdRepository.save(activeRequest);
    }

    public CaptivePortalAdEntity updateStatus(UUID uuid){

        CaptivePortalAdEntity ad = getAdById(uuid);

        if ("Active".equals(ad.getAdStatus())){
         ad.setAdStatus("Inactive");
        }else if("Inactive".equals(ad.getAdStatus())){
         ad.setAdStatus("Active");
        }else {
            ad.setAdStatus("Inactive");
        }

        return captivePortalAdRepository.save(ad);

    }
}
