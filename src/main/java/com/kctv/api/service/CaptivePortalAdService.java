package com.kctv.api.service;


import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.entity.admin.ad.CaptivePortalAdEntity;
import com.kctv.api.repository.ad.CaptivePortalAdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaptivePortalAdService {

    private final CaptivePortalAdRepository captivePortalAdRepository;

    public CaptivePortalAdEntity getAdById(UUID adId){

        return Optional.ofNullable(captivePortalAdRepository.findByAdId(adId)).orElseThrow(CResourceNotExistException::new);

    }
}
